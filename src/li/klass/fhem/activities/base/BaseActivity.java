package li.klass.fhem.activities.base;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.AdapterView;
import android.widget.Toast;
import li.klass.fhem.R;
import li.klass.fhem.activities.PreferencesActivity;
import li.klass.fhem.data.FHEMService;
import li.klass.fhem.domain.Device;
import li.klass.fhem.data.provider.favorites.FavoritesService;

public abstract class BaseActivity<DOMAIN, ADAPTER> extends UpdateableActivity<DOMAIN> {
    public static final int OPTION_UPDATE = 1;
    public static final int OPTION_PREFERENCES = 2;

    public static final int CONTEXT_MENU_FAVORITES_ADD = 1;
    public static final int CONTEXT_MENU_FAVORITES_DELETE = 2;

    protected Device contextMenuClickedDevice;
    private long backPressStart;

    protected ADAPTER adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setLayout();
        adapter = initializeLayoutAndReturnAdapter();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuItem updateItem = menu.add(0, OPTION_UPDATE, 0, getResources().getString(R.string.update));
        updateItem.setIcon(R.drawable.ic_menu_refresh);

        MenuItem preferencesItem = menu.add(0, OPTION_PREFERENCES, 0, getResources().getString(R.string.preferences));
        preferencesItem.setIcon(android.R.drawable.ic_menu_preferences);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        int itemId = item.getItemId();

        switch (itemId) {
            case OPTION_UPDATE:
                update(true);
                break;
            case OPTION_PREFERENCES:
                Intent intent = new Intent(this, PreferencesActivity.class);
                startActivity(intent);
        }

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        update(false);
    }

    @Override
    protected void onStop() {
        super.onStop();
        FHEMService.INSTANCE.storeDeviceListMap();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        Object tag = info.targetView.getTag();

        if (tag == null) return;
        if (tag instanceof Device) {
            contextMenuClickedDevice = (Device) tag;
            Resources resources = getResources();
            menu.add(0, CONTEXT_MENU_FAVORITES_ADD, 0, resources.getString(R.string.context_addtofavorites));
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (event.getRepeatCount() == 0) {
                backPressStart = System.currentTimeMillis();
                Log.e(BaseActivity.class.getName(), "back press start " + backPressStart);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            long diff = System.currentTimeMillis() - backPressStart;
            Log.e(BaseActivity.class.getName(), "back press up " + diff);
            if (diff < 200) {
                super.onBackPressed();
            }
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case CONTEXT_MENU_FAVORITES_ADD:
                FavoritesService.INSTANCE.addFavorite(contextMenuClickedDevice);
                Toast.makeText(this, R.string.context_favoriteadded, Toast.LENGTH_SHORT).show();
                return true;
        }
        return false;
    }

    protected abstract ADAPTER initializeLayoutAndReturnAdapter();
    protected abstract void setLayout();

}