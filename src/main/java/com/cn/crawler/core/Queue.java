package com.cn.crawler.core;

import com.cn.crawler.entities.Link;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;
import java.util.*;

/**
 * Created by burhan on 5/31/17.
 */
public class Queue implements java.util.Queue<Link>{
    private static final Logger log = LoggerFactory.getLogger(Queue.class);

    private final String domain;
    private final Data data;
    private LinkedHashSet<Link> visited = new LinkedHashSet<>();
    private LinkedHashSet<Link> error = new LinkedHashSet<>();
    private LinkedHashSet<Link> queue = new LinkedHashSet<>();
    private List<Link> changes = new ArrayList<>();
    private static final int BATCH_MAX = 1000;

    public Queue(Data data, String domain) {
        this.data = data;
        this.domain = domain;
    }

    public String getDomain() {
        return domain;
    }

    @Override
    public int size() {
        return queue.size();
    }

    public int getVisitedSize(){
        return visited.size();
    }

    @Override
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return queue.contains(o);
    }

    public boolean isVisited(Object o){
        return visited.contains(o);
    }

    @Override
    public Iterator<Link> iterator() {
        return queue.iterator();
    }

    @Override
    public Object[] toArray() {
        return queue.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return (T[]) queue.toArray();
    }

    @Override
    public boolean add(Link link) {
        if(!domain.equalsIgnoreCase(link.getDomain()) || queue.contains(link) || visited.contains(link) || error.contains(link)){
            return false;
        }
        trackChanges(link);
        return queue.add(link);
    }

    @Override
    public boolean remove(Object o) {
        return queue.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return queue.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends Link> c) {
        return queue.addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return queue.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return queue.retainAll(c);
    }

    @Override
    public void clear() {
        visited.clear();
        queue.clear();
    }

    @Override
    public boolean offer(Link link) {
        return add(link);
    }

    @Override
    public Link remove() {
        Iterator<Link> it = iterator();
        if(it.hasNext()){
            Link link = it.next();
            queue.remove(link);
            return link;
        }
        throw new NoSuchElementException();
    }

    @Override
    public Link poll() {
        try {
            return remove();
        }catch (NoSuchElementException ex){

        }
        return null;
    }

    @Override
    public Link element() {
        Iterator<Link> it = iterator();
        if(it.hasNext()){
            Link link = it.next();
            queue.remove(link);
            return link;
        }
        throw new NoSuchElementException();
    }

    @Override
    public Link peek() {
        Iterator<Link> it = iterator();
        if(it.hasNext()){
            Link link = it.next();
            queue.remove(link);
            return link;
        }
        return null;
    }

    public boolean setVisited(Link link){
        link.setStatus(Status.V);
        link.setDate(new Date());
        trackChanges(link);
        return visited.add(link);
    }

    public boolean setError(Link link){
        link.setStatus(Status.E);
        link.setDate(new Date());
        trackChanges(link);
        return error.add(link);
    }

    private void trackChanges(Link link){
        changes.add(link);
        if(changes.size() >= BATCH_MAX){
            log.info("Saving current state for queue : " + domain);
            data.saveLinks(changes);
            changes.clear();
        }
    }

    public void loadState(final boolean update){
        List<Link> links = data.getLinksByDomain(domain);
        if(update) {
            queue.addAll(links);
        } else {
            for (Link link : links) {
                if (link.getStatus() == Status.V) {
                    visited.add(link);
                } else if (link.getStatus() == Status.E) {
                    error.add(link);
                } else {
                    queue.add(link);
                }
            }
        }
        log.info("Loaded state for : " + this.toString());
    }


    public void saveState(){
        Set<Link> all = new HashSet<>(changes);
        all.addAll(queue);
        all.addAll(visited);
        all.addAll(error);
        data.saveLinks(new ArrayList<>(all));
        log.info("Saved state for : " + domain);
    }

    @Override
    public String toString() {
        return "Queue{" +
                "domain='" + domain + '\'' +
                ", visited=" + visited.size() +
                ", error=" + error.size() +
                ", queue=" + queue.size() +
                '}';
    }
}
