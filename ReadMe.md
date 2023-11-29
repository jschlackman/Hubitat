# Pollen Virtual Sensor for Hubitat

A Hubitat virtual device for retrieving pollen index data from [Pollen.com](https://www.pollen.com/). This is a ported and improved version of [PollenThing](https://github.com/jschlackman/PollenThing) that was originally developed for the now-retired SmartThings Classic platform.

## Setup

1. Click **Drivers code** in the hub sidebar, then click the **+ New Driver** button and paste in the full contents of the **pollen-virtual-sensor.groovy** file.

2. Click **Devices** in the hub sidebar, then click the **+ Add Device** button followed by the **Virtual** button to create a new device:
    1. **Device Name**: enter a readable name such as 'Pollen Index'.
    2. **Device Label**: enter any optional label for the device.
    3. **Type**: pick **Pollen Virtual Sensor** from the dropdown (it will be near the bottom of the list).
    4. **Room**: pick a room for the device (optional).

3. Click **Save**. The virtual sensor is now ready to use.

4. (Optional) Refresh the page to view the initial data from the API, then set your preferred attribute in the **Status attribute for Devices/Rooms** dropdown. Suggested attributes are **category** or **index**.

Additional step-by-step instructions for installing driver code can be found in the [Hubitat documentation](https://docs2.hubitat.com/user-interface/developer/drivers-code).

## Use

Once setup is complete an initial query will be made to the API, and you can view the returned pollen index data on the Device page.

The device will refresh data once every 3 hours by default. Pollen.com data is typically not updated more than once per day. If it is not updating for you or you need a more frequent schedule, you can use [Rule Machine](https://docs2.hubitat.com/apps/rule-machine) to poll or refresh the device at a schedule of your choice. The device will attempt to pull new data every time it is polled.

Attribute data is accessible to dashboards and some other apps such as [Rule Machine](https://docs2.hubitat.com/apps/rule-machine) via device attributes. These can be used as inputs for other automations that support reading from devices using the generic **capability.sensor**.

The attributes available from the device are:

| Attribute Name  | Format | Description  |
|---|---|---|
| index | Number | Pollen index for the configured location (0.0-12.0) |
| category | Enum | Category for the reported index number (Low/Medium/High) |
| category | String | Suggested color for the reported index category in hex-color CSS format |
| triggers | String | Comma separated list of the top allergen triggers for the configured location |
| location | String | City or area name for the reported data, with 2-letter state code. May also contain basic error messages when data is unavailable from the API. |

## Acknowledgements

* Thanks to [arsaboo](https://github.com/arsaboo/) for sharing the method for querying the Pollen.com API methods in his HomeAssistant config.
