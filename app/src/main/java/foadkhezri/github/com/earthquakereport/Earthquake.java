package foadkhezri.github.com.earthquakereport;

public class Earthquake {
    private double magnitude;
    private String location;
    private Long date;
    private String url;

    public Earthquake(double magnitude, String location, Long date, String url) {
        this.magnitude = magnitude;
        this.location = location;
        this.date = date;
        this.url = url;
    }

    public double getMagnitude() {
        return magnitude;
    }

    public String getLocation() {
        return location;
    }

    public Long getDate() {
        return date;
    }

    public String getUrl() {return url;}

    @Override
    public String toString() {
        return "Earthquake{" +
                "magnitude=" + magnitude +
                ", location='" + location + '\'' +
                ", date=" + date +
                ", url='" + url + '\'' +
                '}';
    }
}