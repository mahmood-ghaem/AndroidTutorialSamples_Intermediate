package nl.mahmood.expandableanimationrecyclerview;

public class City
{
    private String title;
    private String avatar;
    private String image;
    private String description;
    private String website;
    private boolean expanded;


    public City(String name, String avatar, String image, String description, String website)
    {
        this.title = name;
        this.avatar = avatar;
        this.image = image;
        this.description = description;
        this.website = website;
        this.expanded = false;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getAvatar()
    {
        return avatar;
    }

    public void setAvatar(String avatar)
    {
        this.avatar = avatar;
    }

    public String getImage()
    {
        return image;
    }

    public void setImage(String image)
    {
        this.image = image;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getWebsite()
    {
        return website;
    }

    public void setWebsite(String website)
    {
        this.website = website;
    }

    public boolean isExpanded()
    {
        return expanded;
    }

    public void setExpanded(boolean expanded)
    {
        this.expanded = expanded;
    }
}
