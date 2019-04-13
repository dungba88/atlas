package org.joo.atlas.tasks.impl.algorithm;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.joo.atlas.models.Task;
import org.joo.atlas.models.TaskTopo;
import org.joo.atlas.models.impl.DefaultTaskTopo;
import org.joo.atlas.support.exceptions.CyclicGraphDetectedException;

public class DFSTopoSorting {

    private HashSet<String> tmpMarks;

    private HashSet<String> permMarks;

    private LinkedList<Task> results;

    private Map<String, Task> taskMap;

    private Map<String, Integer> groupMap;

    private Task[] tasks;

    public DFSTopoSorting(Task[] tasks) {
        this.tasks = tasks;
        this.taskMap = Arrays.stream(tasks) //
                             .collect(Collectors.toMap(Task::getId, Function.identity()));
    }

    public DFSTopoSorting sort() {
        this.permMarks = new HashSet<String>();
        this.tmpMarks = new HashSet<String>();
        this.results = new LinkedList<Task>();

        for (var task : tasks) {
            visit(task);
        }

        assignGroup();

        return this;
    }

     void visit(Task task) {
        var id = task.getId();

        if (permMarks.contains(id))
            return;
        if (tmpMarks.contains(id))
            throw new CyclicGraphDetectedException(id);

        tmpMarks.add(id);

        for (var adj : task.getDependants()) {
            visit(taskMap.get(adj));
        }

        tmpMarks.remove(id);
        permMarks.add(id);

        results.addFirst(task);
    }

    private void assignGroup() {
        this.groupMap = new HashMap<>();
        for (var task : tasks) {
            groupMap.put(task.getId(), 0);
        }

        for (var task : results) {
            var parentLevel = groupMap.get(task.getId());
            for (var adj : task.getDependants()) {
                groupMap.compute(adj, (k, v) -> Math.max(v, parentLevel + 1));
            }
        }
    }

    public TaskTopo[] topo() {
        var invertedEdges = new HashMap<String, List<String>>();
        for (var task : tasks) {
            for (var adj : task.getDependants()) {
                invertedEdges.computeIfAbsent(adj, k -> new ArrayList<>()) //
                             .add(task.getId());
            }
        }
        return results.stream() //
                      .map(task -> toTaskTopo(invertedEdges, task)) //
                      .toArray(size -> new TaskTopo[size]);
    }

    private DefaultTaskTopo toTaskTopo(Map<String, List<String>> invertedEdges, Task task) {
        var depended = invertedEdges.getOrDefault(task.getId(), new ArrayList<>());
        return new DefaultTaskTopo(task, groupMap.get(task.getId()), depended.toArray(new String[0]));
    }

    public Task[] results() {
        return this.results();
    }

    public Task[][] group() {
        return groupMap.entrySet().stream() //
                       .collect(groupingBy(Map.Entry::getValue, //
                               mapping(e -> taskMap.get(e.getKey()), toList()))) //
                       .entrySet().stream() //
                       .sorted((e1, e2) -> e1.getKey() - e2.getKey()) //
                       .map(e -> e.getValue().toArray(new Task[0])) //
                       .toArray(size -> new Task[size][]);
    }
}
