package li.klass.fhem.activities.devicelist;

import android.content.res.Resources;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import li.klass.fhem.R;
import li.klass.fhem.domain.Device;
import li.klass.fhem.domain.RoomDeviceList;
import li.klass.fhem.data.provider.favorites.FavoritesService;

public class FavoritesActivity extends DeviceListActivity {

    @Override
    protected RoomDeviceList getCurrentData(boolean refresh) {
        return FavoritesService.INSTANCE.getFavorites(refresh);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        Object tag = info.targetView.getTag();

        if (tag == null) return;
        if (tag instanceof Device) {
            contextMenuClickedDevice = (Device) tag;
            Resources resources = getResources();
            menu.add(0, CONTEXT_MENU_FAVORITES_DELETE, 0, resources.getString(R.string.context_removefavorite));
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case CONTEXT_MENU_FAVORITES_DELETE:
                FavoritesService.INSTANCE.removeFavorite(contextMenuClickedDevice);
                update(false);
                Toast.makeText(this, R.string.context_favoriteremoved, Toast.LENGTH_SHORT).show();
                return true;
        }
        return false;
    }
}