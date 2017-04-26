package marmu.com.mychat.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.quickblox.chat.model.QBChatDialog;

import java.util.ArrayList;

import marmu.com.mychat.R;
import marmu.com.mychat.holder.QBUnreadMessageHolder;

/**
 * Created by azharuddin on 24/4/17.
 */

public class ChatFragmentAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<QBChatDialog> qbChatDialog;

    public ChatFragmentAdapter(Context context, ArrayList<QBChatDialog> qbChatDialog) {
        this.context = context;
        this.qbChatDialog = qbChatDialog;
    }

    @Override
    public int getCount() {
        return qbChatDialog.size();
    }

    @Override
    public Object getItem(int position) {
        return qbChatDialog.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_chat_dialog, null);

            TextView txtTitle, txtMessage;
            ImageView imageView, image_unread;

            txtTitle = (TextView) view.findViewById(R.id.list_chat_dialog_title);
            txtMessage = (TextView) view.findViewById(R.id.list_chat_dialog_message);
            imageView = (ImageView) view.findViewById(R.id.image_chatDialog);
            image_unread = (ImageView) view.findViewById(R.id.image_unread);

            txtTitle.setText(qbChatDialog.get(position).getName());
            txtMessage.setText(qbChatDialog.get(position).getLastMessage());

            ColorGenerator generator = ColorGenerator.MATERIAL;
            int randomColor = generator.getRandomColor();

            TextDrawable.IBuilder builder = TextDrawable.builder().beginConfig()
                    .withBorder(4)
                    .endConfig()
                    .round();
            //get  first character  from chat dialog title for create chat dialog image
            TextDrawable drawable = builder.build(txtTitle.getText().toString().substring(0, 1).toUpperCase(), randomColor);

            imageView.setImageDrawable(drawable);

            //set message unread count
            TextDrawable.IBuilder unreadBuilder = TextDrawable.builder().beginConfig()
                    .withBorder(4)
                    .endConfig()
                    .round();

            int unread_count = QBUnreadMessageHolder.getInstance().getBundle().getInt(qbChatDialog.get(position).getDialogId());
            if (unread_count > 0) {
                TextDrawable unread_drawable = unreadBuilder.build(String.valueOf(unread_count), Color.BLUE);
                image_unread.setImageDrawable(unread_drawable);
            }
        }

        return view;
    }
}
