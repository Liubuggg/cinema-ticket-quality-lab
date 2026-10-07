package com.usercinema.domain;

/**
 * 保存影片的基本信息。
 */
public final class Film
{
    private final String name;
    private final String director;
    private final String actor;
    private final String story;
    private final int durationMinutes;

    public Film(String name, String director, String actor, String story, int durationMinutes)
    {
        this.name = name;
        this.director = director;
        this.actor = actor;
        this.story = story;
        this.durationMinutes = durationMinutes;
    }

    public String getName()
    {
        return name;
    }

    public String getDirector()
    {
        return director;
    }

    public String getActor()
    {
        return actor;
    }

    public String getStory()
    {
        return story;
    }

    public int getDurationMinutes()
    {
        return durationMinutes;
    }
}
