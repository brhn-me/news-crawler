package com.cn.crawler.core;

import com.cn.crawler.entities.Link;
import com.cn.crawler.rules.AbstractExploreRule;
import com.cn.crawler.utils.Utils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.*;

/**
 * Created by burhan on 5/31/17.
 */
public class Queue implements java.util.Queue<Link>{
    private static final Logger log = LoggerFactory.getLogger(Queue.class);

    private final String host;
    private final Data data;
    private LinkedHashSet<Link> visited = new LinkedHashSet<>();
    private LinkedHashSet<Link> error = new LinkedHashSet<>();
    private LinkedHashSet<Link> queue = new LinkedHashSet<>();
    private List<Link> changes = new ArrayList<>();
    private final AbstractExploreRule rule;
    private static final int BATCH_MAX = 1000;

    public Queue(Data data, String host, AbstractExploreRule rule) {
        this.data = data;
        this.host = host;
        this.rule = rule;
    }

    public String getHost() {
        return host;
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
        if(!host.equalsIgnoreCase(link.getHost())){
            return false;
        }
        if(rule != null && !rule.isExplorable(link)){
            return false;
        }
        if(queue.contains(link) || visited.contains(link) || error.contains(link)){
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
            log.info("Saving current state for queue : " + host);
            data.saveLinks(changes);
            changes.clear();
        }
    }

    public void loadState(final boolean update){
        List<Link> links = data.getLinksByDomain(host);
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
        log.info("Saved state for : " + host +", " +this.toString());
    }

    @Override
    public String toString() {
        return "Queue{" +
                "host='" + host + '\'' +
                ", visited=" + visited.size() +
                ", error=" + error.size() +
                ", queue=" + queue.size() +
                '}';
    }




    public static void main(String[] args) {
        LinkedHashSet<Link> queue = new LinkedHashSet<>();

        Link link = null;
        try {
            link = new Link("http://www.prothom-alo.com", 0);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        queue.add(link);

        while(true){
            Iterator<Link> it = queue.iterator();
            if(it.hasNext()){
                link = it.next();
                queue.remove(link);
            }
            System.out.println("Fetching ["+queue.size()+"]: " + link.getUrl());
            try {
                String url = Utils.getEncodedUrl(link.getUrl());
                Connection.Response response = Jsoup
                        .connect(url)
                        .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                        .referrer("http://www.google.com")
                        .timeout(60 * 1000)
                        .execute();

                Document doc = response.parse();

                Elements linkElements = doc.select("a[href]");
                for (Element linkElement : linkElements) {
                    String childUrl = linkElement.absUrl("href");
                    if (!Utils.isNullOrEmpty(childUrl)) {
                        try {
                            Link l = new Link(childUrl, link.getDepth() + 1);
                            queue.add(l);
                        } catch (MalformedURLException e) {
                            log.error(e.getMessage() + link);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }
}
