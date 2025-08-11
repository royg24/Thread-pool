package tests_utils;

import java.util.Comparator;

public class Video implements Comparable<Video>, Comparator<Video> {
    private final String name;
    private final long viewsCount;

    public Video(String name, long viewsCount) {
        this.name = name;
        this.viewsCount = viewsCount;
    }

    @Override
    public int compareTo(Video o) {
        return Long.compare(this.viewsCount, o.viewsCount);
    }

    @Override
    public int compare(Video o1, Video o2) {
        return Long.compare(o1.viewsCount, o2.viewsCount);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Video video)) {
            return false;
        }

        return name.equals(video.name) && viewsCount == video.viewsCount;
    }
}

