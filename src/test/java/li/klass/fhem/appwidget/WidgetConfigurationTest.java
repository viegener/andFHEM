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

package li.klass.fhem.appwidget;

import org.junit.Test;

import li.klass.fhem.appwidget.view.WidgetType;

import static li.klass.fhem.appwidget.WidgetConfiguration.escape;
import static li.klass.fhem.appwidget.WidgetConfiguration.unescape;
import static org.assertj.core.api.Assertions.assertThat;

public class WidgetConfigurationTest {

    @Test
    public void should_serialize_correctly() {
        assertConfigurationSerialization(new WidgetConfiguration(5, WidgetType.DIM, "abc"));
        assertConfigurationSerialization(new WidgetConfiguration(50000, WidgetType.STATUS, "def"));
        assertConfigurationSerialization(new WidgetConfiguration(50000, WidgetType.STATUS, "d#ef"));

        assertConfigurationSerialization(new WidgetConfiguration(50000, WidgetType.STATUS, "def", "hello"));
    }

    @Test
    public void should_escape_hashes_correctly() {
        assertThat(escape("d#ef")).isEqualTo("d\\@ef");
        assertThat(escape("d@ef")).isEqualTo("d@ef");
        assertThat(escape("def")).isEqualTo("def");
        assertThat(escape(null)).isNull();
    }

    @Test
    public void should_unescape_hashes_correctly() {
        assertThat(unescape("d\\@ef")).isEqualTo("d#ef");
        assertThat(unescape("d@ef")).isEqualTo("d@ef");
        assertThat(unescape("def")).isEqualTo("def");
        assertThat(unescape(null)).isNull();
    }

    @Test
    public void should_deserialize_deprecated_WidgetConfigurations_with_payload_correctly() {
        WidgetConfiguration configuration = WidgetConfiguration.fromSaveString("123#myDevice#" + WidgetType.STATUS.name() + "#abc");

        assertThat(configuration.isOld).isTrue();
        assertThat(configuration.widgetId).isEqualTo(123);
        assertThat(configuration.payload).contains("myDevice", "abc");
        assertThat(configuration.payload).hasSize(2);
        assertThat(configuration.widgetType).isEqualTo(WidgetType.STATUS);
    }

    @Test
    public void should_deserialize_deprecated_WidgetConfigurations_without_payload_correctly() {
        WidgetConfiguration configuration = WidgetConfiguration.fromSaveString("123#myDevice#" + WidgetType.STATUS.name());

        assertThat(configuration.isOld).isTrue();
        assertThat(configuration.widgetId).isEqualTo(123);
        assertThat(configuration.payload).contains("myDevice");
        assertThat(configuration.payload).hasSize(1);
        assertThat(configuration.widgetType).isEqualTo(WidgetType.STATUS);
    }


    @Test
    public void should_deserialize_new_WidgetConfiguration_correctly() {
        WidgetConfiguration configuration = WidgetConfiguration.fromSaveString("123#" + WidgetType.STATUS.name() + "#abc");

        assertThat(configuration.isOld).isFalse();
        assertThat(configuration.widgetId).isEqualTo(123);
        assertThat(configuration.payload).contains("abc");
        assertThat(configuration.payload).hasSize(1);
        assertThat(configuration.widgetType).isEqualTo(WidgetType.STATUS);
    }

    @Test
    public void should_handle_WidgetConfigurations_without_Payload() {
        WidgetConfiguration configuration = WidgetConfiguration.fromSaveString("123#" + WidgetType.STATUS.name());

        assertThat(configuration.isOld).isFalse();
        assertThat(configuration.widgetId).isEqualTo(123);
        assertThat(configuration.payload).isEmpty();
        assertThat(configuration.widgetType).isEqualTo(WidgetType.STATUS);
    }

    private void assertConfigurationSerialization(WidgetConfiguration widgetConfiguration) {
        String saveString = widgetConfiguration.toSaveString();
        WidgetConfiguration loaded = WidgetConfiguration.fromSaveString(saveString);

        assertThat(loaded.payload).isEqualTo(widgetConfiguration.payload);
        assertThat(loaded.widgetId).isEqualTo(widgetConfiguration.widgetId);
        assertThat(loaded.widgetType).isEqualTo(widgetConfiguration.widgetType);
    }
}
