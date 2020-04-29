package nl.mahmood.newsapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewsContent
{

    /**
     * An array of sample (dummy) items.
     */
    //public static final List<NewsItem> ITEMS = new ArrayList<NewsItem>();

    public static final List<String> ITEMS = new ArrayList<>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, NewsItem> ITEM_MAP = new HashMap<String, NewsItem>();

    static
    {
        // Add some sample items.
//        for (int i = 1; i <= COUNT; i++)
//        {
//            addItem(createDummyItem(i));
//        }
//        DownloadTask task = new DownloadTask();
//        JSONArray jsonArray;
//        try
//        {
//
//            jsonArray = new JSONArray(task.execute("http://newsapi.org/v2/top-headlines?country=nl&apiKey=f1add8eaf65945daa34f68c868c95908"));
//            for (int i = 0; i < jsonArray.length(); i++)
//            {
//                JSONObject jsonPart = jsonArray.getJSONObject(i);
//                String title = jsonPart.getString("title");
//                String content = jsonPart.getString("content");
//                addItem(new NewsItem(String.valueOf(i), title, content));
//            }
//        }catch (Exception e)
//        {
//            e.printStackTrace();
//        }


    }

    public static void addItem(NewsContent.NewsItem item)
    {
        ITEMS.add(item.title);
        ITEM_MAP.put(item.id, item);
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class NewsItem
    {
        public final String id;
        public final String title;
        public final String content;

        public NewsItem(String id, String title, String content)
        {
            this.id = id;
            this.title = title;
            this.content = content;
        }

        @Override
        public String toString()
        {
            return content;
        }
    }
}
