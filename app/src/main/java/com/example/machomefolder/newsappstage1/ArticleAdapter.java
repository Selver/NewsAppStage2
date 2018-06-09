package com.example.machomefolder.newsappstage1;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class ArticleAdapter extends ArrayAdapter<Article> {

    public ArticleAdapter(Context context, ArrayList articles) {
        super(context, 0, articles);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }
        Article current_article = (Article) getItem(position);

        // Find the TextView and set the data
        TextView titleTextView = (TextView) listItemView.findViewById(R.id.title);
        titleTextView.setText(current_article.getTitle());

        // Find the TextView and set the data
        TextView sectionTextView = (TextView) listItemView.findViewById(R.id.section);
        sectionTextView.setText(current_article.getSection());

        // Find the TextView and set the data
        TextView authorTextView = (TextView) listItemView.findViewById(R.id.author);
        authorTextView.setText(current_article.getAuthor());

        ImageView imageView = listItemView.findViewById(R.id.image);
        Picasso.get().load(current_article.getImage()).into(imageView);

        // Find the TextView and set the data
        TextView dateTextView = (TextView) listItemView.findViewById(R.id.date);
        dateTextView.setText(current_article.getDate());

        return listItemView;
    }
}
