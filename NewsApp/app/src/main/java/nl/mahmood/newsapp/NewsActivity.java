package nl.mahmood.newsapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

public class NewsActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        int newsId;

        Intent intent = getIntent();
        newsId = intent.getIntExtra("newsId", -1);

        Cursor c = MainActivity.newsDB.rawQuery("SELECT * FROM tblNews WHERE id=" + newsId, null);

        StringBuffer buffer = new StringBuffer();
        buffer.append("<HTML><HEAD><style> \n" +
                "#myImg {\n" +
                "  width: 200px;\n" +
                "  height: 400px;\n" +
                "  object-fit: cover;\n" +
                "  object-position: 0% 0%;\n" +
                "  animation: mymove 5s infinite;\n" +
                "}  \n" +
                "\n" +
                "@keyframes mymove {\n" +
                "  0% {\n" +
                "    object-position: 0% 0%;\n" +
                "  }\n" +
                "  25% {\n" +
                "    object-position: 20% 0%;\n" +
                "  }\n" +
                "  100% {\n" +
                "    object-position: 100% 100%;\n" +
                "  }\n" +
                "}\n" +
                "</style></HEAD><BODY>");
        if (c.moveToFirst())
        {

            do
            {
                buffer.append("<img src=\"" + c.getString(4) + "\" id=\"myImg\" style=\"display: block;margin-left: auto;margin-right: auto;width: 70%;\">");
                buffer.append("<h3>" + c.getString(1) + "</h3>");
                buffer.append("<p style=\"text-align: justify; text-justify: inter-word;\">" + c.getString(2) + "</p>");
                buffer.append("<p align=\"right\"><a href=\"" + c.getString(3) + "\">Read More</a></p>");
            } while (c.moveToNext());
        }

        buffer.append("</BODY></HTML>");

        WebView webView = findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient());
        webView.loadData(buffer.toString(), "text/html", "UTF-8");

    }
}
