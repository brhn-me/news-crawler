package com.cn.crawler.core;

import com.cn.crawler.entities.Link;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by burhan on 5/31/17.
 */
public class UniquePriorityQueue extends PriorityQueue<Link> {
    private static final Logger log = LoggerFactory.getLogger(UniquePriorityQueue.class);
    private Set<Link> set = new HashSet<>();

    public UniquePriorityQueue() {
        super((o1, o2) -> {
            return o2.getPriority() - o1.getPriority();
        });
    }

    @Override
    public boolean add(Link link) {
        if (set.contains(link)) {
            return false;
        }
        return super.add(link);
    }

    @Override
    public boolean contains(Object o) {
        return set.contains(o);
    }

    @Override
    public boolean remove(Object o) {
        set.remove(o);
        return super.remove(o);
    }

    @Override
    public void clear() {
        set.clear();
        super.clear();
    }

    @Override
    public boolean offer(Link link) {
        set.add(link);
        return super.offer(link);
    }

    @Override
    public Link peek() {
        Link link = super.peek();
        if (link != null) {
            set.remove(link);
        }
        return link;
    }

    @Override
    public Link poll() {
        Link link = super.poll();
        if (link != null) {
            set.remove(link);
        }
        return link;
    }

    @Override
    public boolean addAll(Collection<? extends Link> c) {
        set.addAll(c);
        return super.addAll(c);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return super.containsAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        set.removeAll(c);
        return super.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        set.retainAll(c);
        return super.retainAll(c);
    }
}
