/*
 * AndFHEM - Open Source Android application to control a FHEM home automation
 * server.
 *
 * Copyright (c) 2011, Matthias Klass or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Inc.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU GENERAL PUBLIC LICENSE, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU GENERAL PUBLIC LICENSE
 * for more details.
 *
 * You should have received a copy of the GNU GENERAL PUBLIC LICENSE
 * along with this distribution; if not, write to:
 *   Free Software Foundation, Inc.
 *   51 Franklin Street, Fifth Floor
 *   Boston, MA  02110-1301  USA
 */

package li.klass.fhem.adapter.devices.genericui.multimedia;

import android.content.Context;

import li.klass.fhem.R;
import li.klass.fhem.adapter.devices.genericui.StateChangingYesNoTwoButtonActionRow;
import li.klass.fhem.adapter.uiservice.StateUiService;
import li.klass.fhem.domain.core.FhemDevice;
import li.klass.fhem.domain.multimedia.VolumeDevice;

public class MuteActionRow<V extends FhemDevice<V> & VolumeDevice> extends StateChangingYesNoTwoButtonActionRow<V> {
    public MuteActionRow(StateUiService stateUiService) {
        super(stateUiService, R.string.musicMute);
    }

    @Override
    protected String getOnStateName(V device, Context context) {
        return "mute";
    }

    @Override
    protected String getOffStateName(V device, Context context) {
        return "volume " + device.getVolumeAsInt();
    }

    @Override
    public boolean isYes(V device) {
        return device.isMuted();
    }
}
