/**
 *  Pollen Virtual Sensor
 *
 *  Author: jschlackman (james@schlackman.org)
 *  Version: 0.1
 *  Date: 2023-11-29
 *
 */
metadata {
	definition (
        name: "Pollen Virtual Sensor",
        namespace: "jschlackman",
        author: "James Schlackman",
        description: "Retrieves airborne pollen data from Pollen.com",
        documentationLink: "https://github.com/jschlackman/PollenThing"
    ) {
		capability "Sensor"
		capability "Polling"

		attribute "index", "number"
		attribute "category", "enum", ["Low","Low-Medium","Medium","Medium-High","High"]
        attribute "categoryColor", "string"
		attribute "triggers", "string"
		attribute "location", "string"

		command "refresh"
	}

	preferences {
		input name: "zipCode", type: "text", title: "Zip Code (optional)", required: false
	}
}

// Parse events into attributes. This will never be called but needs to be present in the DTH code.
def parse(String description) {
	log.debug("Pollen Sensor: Parsing '${description}'")
}

def installed() {
	runEvery3Hours(poll)
	poll()
}

def updated() {
	poll()
}

def uninstalled() {
	unschedule()
}

// handle commands
def poll() {
	def pollenZip = null

	// Use hub zipcode if user has not defined their own
	if(zipCode) {
		pollenZip = zipCode
	} else {
		pollenZip = location.zipCode
	}
	
	log.debug("Getting pollen data for ZIP: ${pollenZip}")

	// Set up the Pollen.com API query
	def params = [
		uri: 'https://www.pollen.com/api/forecast/current/pollen/',
		path: pollenZip,
		headers: [Referer:'https://www.pollen.com']
	]

	try {
	// Send query to the Pollen.com API
		httpGet(params) {resp ->

            // If we got a valid response
            if (resp.data.ForecastDate) {
            
                log.debug("Forecast retreived for ${resp.data.ForecastDate}")
		        
                // Parse the periods data array
			    resp.data.Location.periods.each {period ->
				
				    // Only interested in today's forecast
				    if (period.Type == 'Today') {
					
					    // Pollen index
                        send(name: "index", value: period.Index, descriptionText: "Pollen index is ${period.Index}")
					
					    def catName = ""
                        def catColor = ""
					    def indexNum = period.Index.toFloat()
					
					    // Set the category according to index thresholds
					    if (indexNum < 2.5) {catName = "Low"; catColor ="#90d2a7"}
					    else if (indexNum < 4.9) {catName = "Low-Medium"; catColor ="#44b621"}
					    else if (indexNum < 7.3) {catName = "Medium"; catColor ="#f1d801"}
					    else if (indexNum < 9.7) {catName = "Medium-High"; catColor ="#d04e00"}
					    else if (indexNum < 12) {catName = "High"; catColor ="#bc2323"}
					    else {catName = "Unknown"; catColor ="#000000"}
				
                        send(name: "category", value: catName, descriptionText: "Pollen level is ${catName}")
                        send(name: "categoryColor", value: catColor, descriptionText: "Pollen level color definition for display")
					
					    // Build the list of allergen triggers
					    def triggersList = period.Triggers.inject([]) { result, entry ->
						    result << "${entry.Name}"
					    }.join(", ")
					
					    send(name: "triggers", value: triggersList, descriptionText: "Top allergens in the reported location are ${triggersList}")
				    }

				    // Forecast location
				    send(name: "location", value: resp.data.Location.DisplayLocation, descriptionText: "Current allergy report for ${resp.data.Location.DisplayLocation}")
                }
            } else {
                
                log.error("No valid data returned from Pollen.com API")
                log.debug(resp);
                
            }
		}
	}
	catch (SocketTimeoutException e) {
		log.error("Connection to Pollen.com API timed out.")
		send(name: "location", value: "Timed out", descriptionText: "Connection timed out while retrieving data from API")
	}
	catch (e) {
		log.error("Could not retrieve pollen data: $e")
		send(name: "location", value: "Unknown error", descriptionText: "Could not retrieve data from API")
	}
}

def refresh() {
	poll()
}

def configure() {
	poll()
}

private send(map) {
	//log.debug("Pollen: event: $map")
	sendEvent(map)
}