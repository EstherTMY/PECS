package cn.edu.wku.esthertang.pecs;

/**
 * Created by esthertang on 05/12/2017.
 */

public class Item {
    private String category;
    private String id;
    private String time;
    private String location;

    public Item() {
    }

    public Item(String category, String id, String time, String location) {
        this.category = category;
        this.id = id;
        this.time = time;
        this.location = location;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCategory() {
        return category;
    }

    public String getId() {
        return id;
    }

    public String getTime() {
        return time;
    }

    public String getLocation() {
        return location;
    }

}
