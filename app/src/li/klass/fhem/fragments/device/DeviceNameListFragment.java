/*
 * AndFHEM - Open Source Android application to control a FHEM home automation
 *  server.
 *
 *  Copyright (c) 2012, Matthias Klass or third-party contributors as
 *  indicated by the @author tags or express copyright attribution
 *  statements applied by the authors.  All third-party contributions are
 *  distributed under license by Red Hat Inc.
 *
 *  This copyrighted material is made available to anyone wishing to use, modify,
 *  copy, or redistribute it subject to the terms and conditions of the GNU GENERAL PUBLICLICENSE, as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 *  or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU GENERAL PUBLIC LICENSE
 *  for more details.
 *
 *  You should have received a copy of the GNU GENERAL PUBLIC LICENSE
 *  along with this distribution; if not, write to:
 *    Free Software Foundation, Inc.
 *    51 Franklin Street, Fifth Floor
 */

package li.klass.fhem.fragments.device;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import li.klass.fhem.R;
import li.klass.fhem.activities.device.DeviceNameListAdapter;
import li.klass.fhem.constants.Actions;
import li.klass.fhem.constants.BundleExtraKeys;
import li.klass.fhem.constants.ResultCodes;
import li.klass.fhem.domain.RoomDeviceList;
import li.klass.fhem.domain.core.Device;
import li.klass.fhem.domain.core.DeviceType;
import li.klass.fhem.fragments.core.BaseFragment;
import li.klass.fhem.widget.GridViewWithSections;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public abstract class DeviceNameListFragment extends BaseFragment {
    private transient DeviceNameListAdapter adapter;

    public interface DeviceFilter extends Serializable {
        boolean isSelectable(Device<?> device);
    }

    @SuppressWarnings("unused")
    public DeviceNameListFragment(Bundle bundle) {
        super(bundle);
    }

    @SuppressWarnings("unused")
    public DeviceNameListFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View superView = super.onCreateView(inflater, container, savedInstanceState);
        if (superView != null) return superView;

        View view = inflater.inflate(R.layout.device_name_list, null);
        GridViewWithSections deviceList = (GridViewWithSections) view.findViewById(R.id.deviceMap1);

        adapter = new DeviceNameListAdapter(inflater.getContext(), new RoomDeviceList(""));
        adapter.registerOnClickObserver(new GridViewWithSections.GridViewWithSectionsOnClickObserver() {
            @Override
            public void onItemClick(View view, Object parent, Object child, int parentPosition, int childPosition) {
                onDeviceNameClick((DeviceType) parent, (Device<?>) child);
            }
        });
        deviceList.setAdapter(adapter);

        return view;
    }

    protected abstract void onDeviceNameClick(DeviceType parent, Device<?> child);

    private void filterDevices(RoomDeviceList roomDeviceList) {
        DeviceFilter deviceFilter = getDeviceFilter();
        if (deviceFilter == null) return;

        for (Device<?> device : roomDeviceList.getAllDevices()) {
            if (! deviceFilter.isSelectable(device)) {
                roomDeviceList.removeDevice(device);
            }
        }
    }

    @Override
    public void update(boolean doUpdate) {
        Intent allDevicesIntent = new Intent(Actions.GET_ALL_ROOMS_DEVICE_LIST);
        allDevicesIntent.putExtras(new Bundle());
        allDevicesIntent.putExtra(BundleExtraKeys.DO_REFRESH, false);
        allDevicesIntent.putExtra(BundleExtraKeys.RESULT_RECEIVER, new ResultReceiver(new Handler()) {
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                super.onReceiveResult(resultCode, resultData);

                if (resultCode != ResultCodes.SUCCESS || !resultData.containsKey(BundleExtraKeys.DEVICE_LIST)) {
                    return;
                }

                RoomDeviceList roomDeviceList = (RoomDeviceList) resultData.getSerializable(BundleExtraKeys.DEVICE_LIST);
                filterDevices(roomDeviceList);

                String selectedDevice = (String) creationAttributes.get(BundleExtraKeys.DEVICE_NAME);

                Set<Device> allDevices = roomDeviceList.getAllDevices();
                if (allDevices.size() > 0) {
                    adapter.updateData(roomDeviceList, selectedDevice);
                }
            }
        });
        getActivity().startService(allDevicesIntent);
    }

    private DeviceFilter getDeviceFilter() {
        return (DeviceFilter) creationAttributes.get(BundleExtraKeys.DEVICE_FILTER);
    }

    @Override
    protected void onContentChanged(Map<String, Serializable> oldCreationAttributes, final Map<String, Serializable> newCreationAttributes) {
        if (oldCreationAttributes == null && newCreationAttributes.containsKey(BundleExtraKeys.DEVICE_FILTER)) {
            newCreationAttributes.put(BundleExtraKeys.ORIGINAL_DEVICE_FILTER, newCreationAttributes.get(BundleExtraKeys.DEVICE_FILTER));
        }

        if (oldCreationAttributes != null && ! doContentChangedAttributesMatch(oldCreationAttributes, newCreationAttributes, BundleExtraKeys.ROOM_NAME)) {
            final DeviceFilter oldDeviceFilter = (DeviceFilter) oldCreationAttributes.get(BundleExtraKeys.ORIGINAL_DEVICE_FILTER);
            newCreationAttributes.put(BundleExtraKeys.ORIGINAL_DEVICE_FILTER, oldDeviceFilter);
            newCreationAttributes.put(BundleExtraKeys.DEVICE_FILTER, new DeviceFilter() {
                @Override
                public boolean isSelectable(Device<?> device) {
                    if (oldDeviceFilter != null && ! oldDeviceFilter.isSelectable(device)) {
                        return false;
                    }
                    String roomName = (String) newCreationAttributes.get(BundleExtraKeys.ROOM_NAME);
                    return device.getRoom().equals(roomName);
                }
            });
            update(false);
        }

        updateIfAttributesDoNotMatch(oldCreationAttributes, newCreationAttributes, BundleExtraKeys.DEVICE_NAME);
        updateIfAttributesDoNotMatch(oldCreationAttributes, newCreationAttributes, BundleExtraKeys.ROOM_NAME);

        super.onContentChanged(oldCreationAttributes, newCreationAttributes);
    }
}