package marmu.com.mychat.action_mode;

import android.content.Context;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

import com.quickblox.chat.model.QBChatDialog;

import marmu.com.mychat.R;
import marmu.com.mychat.adapter.ChatFragmentAdapter;
import marmu.com.mychat.fragment.ChatFragment;

/**
 * Created by azharuddin on 27/4/17.
 */

public class ChatFragmentActionMode implements ActionMode.Callback {

    private Context context;
    private ChatFragmentAdapter chatFragmentAdapter;

    public ChatFragmentActionMode(Context context, ChatFragmentAdapter chatFragmentAdapter) {
        this.context = context;
        this.chatFragmentAdapter = chatFragmentAdapter;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.chat_dialog_context_menu, menu);//Inflate the menu over action mode
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {

        //Sometimes the meu will not be visible so for that we need to set their visibility manually in this method
        //So here show action menu according to SDK Levels
        menu.findItem(R.id.action_dialog_delete).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_dialog_delete:
                ChatFragment chatFragment = new ChatFragment();//Get list view Fragment
                chatFragment.deleteRows(context);//delete selected rows

                break;
        }
        return false;
    }


    @Override
    public void onDestroyActionMode(ActionMode mode) {
        //When action mode destroyed remove selected selections and set action mode to null
        chatFragmentAdapter.removeSelection();  // remove selection
        ChatFragment chatFragment = new ChatFragment();//Get list fragment
        chatFragment.setNullToActionMode();//Set action mode null
    }
}
