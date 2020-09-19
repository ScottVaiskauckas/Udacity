package com.sitstaycreate.android.newsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * An {@link ArticleAdapter} knows how to create a list item layout for each article
 * in the data source (a list of {@link Article} objects).
 *
 * These list item layouts will be provided to an adapter view like ListView
 * to be displayed to the user.
 */
public class ArticleAdapter extends ArrayAdapter<Article> {

    /**
     * Constructs a new {@link ArticleAdapter}.
     *
     * @param context of the app
     * @param articles is the list of articles, which is the data source of the adapter
     */
    public ArticleAdapter(Context context, List<Article> articles) {
        super(context, 0, articles);
    }

    /**
     * Returns a list item view that displays information about the article at the given position
     * in the list of articles.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.article_list_item, parent, false);
        }

        // Find the article at the given position in the list of articles
        Article currentArticle = getItem(position);

        // Find the TextView with view ID title
        TextView titleTextView = (TextView) listItemView.findViewById(R.id.title);

        // Display the title of the current article in that TextView
        titleTextView.setText(currentArticle.getTitle());

        // Find the TextView with view ID author
        TextView authorTextView = (TextView) listItemView.findViewById(R.id.author);

        // Display the author of the current article in that TextView
        authorTextView.setText(currentArticle.getAuthor());

        // Find the TextView with view ID section
        TextView sectionTextView = (TextView) listItemView.findViewById(R.id.section);

        // Display the section of the current article in that TextView
        sectionTextView.setText(currentArticle.getSection());

        // Find the TextView with view ID date_published
        TextView datePublishedTextView = (TextView) listItemView.findViewById(R.id.date_published);

        //Get date article was published from Json and separate date from time
        String dateString = currentArticle.getDatePublished().substring(0, 10);

        //Split string
        String dateStringArray[] = dateString.split("-");

        //Construct string so it follows mm/dd/yyyy format
        String datePublishedString = dateStringArray[1] + '/' + dateStringArray[2] + '/' + dateStringArray[0];

        // Display the date the current article was published in that TextView
        datePublishedTextView.setText(datePublishedString);

        // Return the list item view that is now showing the appropriate data
        return listItemView;
    }
}
