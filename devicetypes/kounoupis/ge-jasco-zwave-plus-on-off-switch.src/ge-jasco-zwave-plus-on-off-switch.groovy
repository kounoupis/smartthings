/**
 *  GE/Jasco Z-Wave Plus On/Off Switch
 *
 *  Copyright 2017 Chris Nussbaum
 *  Copyright 2021 Marios Hadjieleftheriou
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 *  Author: Chris Nussbaum
 *  Date: 03/19/2017
 *  Author: Marios Hadjieleftheriou
 *  Date: 02/23/2021
 *
 *  Changelog:
 *
 *  0.10 (02/23/2021) -    Initial 0.1 Beta.
 *
 *
 *   Button Mappings:
 *
 *   ACTION          BUTTON#    BUTTON ACTION
 *   Double-Tap Up     1        pressed
 *   Double-Tap Down   2        pressed
 *
 */
metadata {
    definition (name: "GE/Jasco Z-Wave Plus On/Off Switch", namespace: "kounoupis", author: "Marios Hadjieleftheriou") {
        capability "Actuator"
        capability "Button"
        capability "Configuration"
        capability "Indicator"
        capability "Polling"
        capability "Refresh"
        capability "Sensor"
        capability "Switch"

        attribute "inverted", "enum", ["inverted", "not inverted"]
        
        command "doubleUp"
        command "doubleDown"
        command "inverted"
        command "notInverted"
        
        // These include version because there are older firmwares that don't support double-tap or the extra association groups
        fingerprint mfr:"0063", prod:"4952", model: "3036", ver: "5.20", deviceJoinName: "GE Z-Wave Plus Wall Switch"
        fingerprint mfr:"0063", prod:"4952", model: "3036", ver: "5.22", deviceJoinName: "GE Z-Wave Plus Wall Switch"
        fingerprint mfr:"0063", prod:"4952", model: "3037", ver: "5.20", deviceJoinName: "GE Z-Wave Plus Toggle Switch"
        fingerprint mfr:"0063", prod:"4952", model: "3038", ver: "5.20", deviceJoinName: "GE Z-Wave Plus Toggle Switch"
        fingerprint mfr:"0063", prod:"4952", model: "3130", ver: "5.20", deviceJoinName: "Jasco Z-Wave Plus Wall Switch"
        fingerprint mfr:"0063", prod:"4952", model: "3131", ver: "5.20", deviceJoinName: "Jasco Z-Wave Plus Toggle Switch"
        fingerprint mfr:"0063", prod:"4952", model: "3132", ver: "5.20", deviceJoinName: "Jasco Z-Wave Plus Toggle Switch"
    }

    simulator {
        status "on":  "command: 2003, payload: FF"
        status "off": "command: 2003, payload: 00"

        // reply messages
        reply "2001FF,delay 5000,2602": "command: 2603, payload: FF"
        reply "200100,delay 5000,2602": "command: 2603, payload: 00"
    }

    preferences {
        input (
            type: "paragraph",
            element: "paragraph",
            title: "Configure Association Groups:",
            description: "Devices in association group 2 will receive Basic Set commands directly from the switch when it is turned on or off. Use this to control another device as if it was connected to this switch.\n\n" +
                         "For executing scenes on double tap up/down, provide the scene ids (https://smartthings.developer.samsung.com/docs/devices/working-with-scenes.html) separated by comma. Empty strings are allowed."
        )

        input (
            name: "requestedGroup2",
            title: "Association Group 2 Members (Max of 5):",
            type: "text",
            required: false
        )

        input (
            name: "requestedGroup3",
            title: "Scene ids to execute on double tap up/down (comma separated):",
            type: "text",
            required: false
        )
    }

    tiles(scale:2) {
        multiAttributeTile(name:"switch", type: "lighting", width: 6, height: 4, canChangeIcon: true){
            tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
                attributeState "on", label: '${name}', action: "switch.off", icon: "https://raw.githubusercontent.com/kounoupis/SmartThingsPublic/master/devicetypes/kounoupis/SwitchOnIcon.png", backgroundColor: "#00a0dc", nextState:"turningOff"
                attributeState "off", label: '${name}', action: "switch.on", icon: "https://raw.githubusercontent.com/kounoupis/SmartThingsPublic/master/devicetypes/kounoupis/SwitchOffIcon.png", backgroundColor: "#ffffff", nextState:"turningOn"
                attributeState "turningOn", label:"Turning On", action:"switch.off", icon:"https://raw.githubusercontent.com/kounoupis/SmartThingsPublic/master/devicetypes/kounoupis/SwitchOnIcon.png", backgroundColor:"#00a0dc", nextState:"turningOff"
                attributeState "turningOff", label:"Turning Off", action:"switch.on", icon:"https://raw.githubusercontent.com/kounoupis/SmartThingsPublic/master/devicetypes/kounoupis/SwitchOffIcon.png", backgroundColor:"#ffffff", nextState:"turningOn"
            }
        }

        standardTile("doubleUp", "device.button", width: 3, height: 2, decoration: "flat") {
            state "default", label: "Tap ▲▲", backgroundColor: "#ffffff", action: "doubleUp", icon: "https://raw.githubusercontent.com/kounoupis/SmartThingsPublic/master/devicetypes/kounoupis/SwitchOnIcon.png"
        }     

        standardTile("doubleDown", "device.button", width: 3, height: 2, decoration: "flat") {
            state "default", label: "Tap ▼▼", backgroundColor: "#ffffff", action: "doubleDown", icon: "https://raw.githubusercontent.com/kounoupis/SmartThingsPublic/master/devicetypes/kounoupis/SwitchOffIcon.png"
        } 

        standardTile("indicator", "device.indicatorStatus", width: 2, height: 2, inactiveLabel: false, decoration: "flat") {
            state "when off", action:"indicator.indicatorWhenOn", icon:"st.indicators.lit-when-off"
            state "when on", action:"indicator.indicatorNever", icon:"st.indicators.lit-when-on"
            state "never", action:"indicator.indicatorWhenOff", icon:"st.indicators.never-lit"
        }

        standardTile("inverted", "device.inverted", width: 2, height: 2, inactiveLabel: false, decoration: "flat") {
            state "not inverted", label: "Not Inverted", action:"inverted", icon:"https://raw.githubusercontent.com/kounoupis/SmartThingsPublic/master/devicetypes/kounoupis/SwitchNotInverted.png", backgroundColor: "#ffffff"
            state "inverted", label: "Inverted", action:"notInverted", icon:"https://raw.githubusercontent.com/kounoupis/SmartThingsPublic/master/devicetypes/kounoupis/SwitchInverted.png", backgroundColor: "#ffffff"
        }

        standardTile("refresh", "device.switch", width: 2, height: 2, inactiveLabel: false, decoration: "flat") {
            state "default", label:'', action:"refresh.refresh", icon:"st.secondary.refresh"
        }

        main(["switch"])
        details(["switch", "doubleUp", "doubleDown", "indicator", "inverted", "refresh"])
    }
}

include 'asynchttp_v1'

// parse events into attributes
def parse(String description) {
    log.debug "description: $description"
    def result = null
    def cmd = zwave.parse(description, [0x20: 1, 0x25: 1, 0x56: 1, 0x70: 2, 0x72: 2, 0x85: 2, 0x5B: 1])
    if (cmd) {
        result = zwaveEvent(cmd)
        log.debug "Parsed ${cmd} to ${result.inspect()}"
    } else {
        log.debug "Non-parsed event: ${description}"
    }
    result    
}

def zwaveEvent(physicalgraph.zwave.commands.crc16encapv1.Crc16Encap cmd) {
    log.debug("zwaveEvent(): CRC-16 Encapsulation Command received: ${cmd}")
    def encapsulatedCommand = zwave.commandClass(cmd.commandClass)?.command(cmd.command)?.parse(cmd.data)
    if (!encapsulatedCommand) {
        log.debug("zwaveEvent(): Could not extract command from ${cmd}")
    } else {
        return zwaveEvent(encapsulatedCommand)
    }
}

def zwaveEvent(physicalgraph.zwave.commands.basicv1.BasicReport cmd) {
    log.debug "---BASIC REPORT V1--- ${device.displayName} sent ${cmd}"
    createEvent(name: "switch", value: cmd.value ? "on" : "off", type: "physical")
}

def zwaveEvent(physicalgraph.zwave.commands.basicv1.BasicSet cmd) {
    log.debug "---BASIC SET V1--- ${device.displayName} sent ${cmd}"
    log.debug "---BASIC SET V1--- currentGroup3 is ${state.currentGroup3}"

    def params = [
        uri: "https://api.smartthings.com",
        path: "",
        body: "1",
        requestBodyType: "application/json",
        headers: ["Authorization": "Bearer ${state.oauth_token}"]
    ]


    if (cmd.value == 255) {
        //createEvent(name: "button", value: "pushed", data: [buttonNumber: 1], descriptionText: "Double-tap up (button 1) on $device.displayName", isStateChange: true, type: "physical")
        if (state.doubleTapUpScene != "") {
            params.path = "/v1/scenes/${state.doubleTapUpScene}/execute"
        } else {
            params.path = ""
        }
    }
    else if (cmd.value == 0) {
        //createEvent(name: "button", value: "pushed", data: [buttonNumber: 2], descriptionText: "Double-tap down (button 2) on $device.displayName", isStateChange: true, type: "physical")
        if (state.doubleTapDownScene != "") {
            params.path = "/v1/scenes/${state.doubleTapDownScene}/execute"
        } else {
            params.path = ""
        }
    }

    if (params.path != "") {
        asynchttp_v1.post('postWithJsonHandler', params)
    }
}

def postWithJsonHandler(response, data) {
    log.debug "response data for post with JSON: ${response.json}"
}

def zwaveEvent(physicalgraph.zwave.commands.associationv2.AssociationReport cmd) {
    log.debug "---ASSOCIATION REPORT V2--- ${device.displayName} sent groupingIdentifier: ${cmd.groupingIdentifier} maxNodesSupported: ${cmd.maxNodesSupported} nodeId: ${cmd.nodeId} reportsToFollow: ${cmd.reportsToFollow}"
    if (cmd.groupingIdentifier == 3) {
        if (cmd.nodeId.contains(zwaveHubNodeId)) {
            createEvent(name: "numberOfButtons", value: 2, displayed: false)
        }
        else {
            sendHubCommand(new physicalgraph.device.HubAction(zwave.associationV2.associationSet(groupingIdentifier: 3, nodeId: zwaveHubNodeId).format()))
            sendHubCommand(new physicalgraph.device.HubAction(zwave.associationV2.associationGet(groupingIdentifier: 3).format()))
            createEvent(name: "numberOfButtons", value: 0, displayed: false)
        }
    }
}

def zwaveEvent(physicalgraph.zwave.commands.configurationv2.ConfigurationReport cmd) {
    log.debug "---CONFIGURATION REPORT V2--- ${device.displayName} sent ${cmd}"
    def name = ""
    def value = ""
    def reportValue = cmd.configurationValue[0]
    switch (cmd.parameterNumber) {
        case 3:
            name = "indicatorStatus"
            value = reportValue == 1 ? "when on" : reportValue == 2 ? "never" : "when off"
            break
        case 4:
            name = "inverted"
            value = reportValue == 1 ? "true" : "false"
            break
        default:
            break
    }
    createEvent([name: name, value: value, displayed: false])
}

def zwaveEvent(physicalgraph.zwave.commands.switchbinaryv1.SwitchBinaryReport cmd) {
    log.debug "---BINARY SWITCH REPORT V1--- ${device.displayName} sent ${cmd}"
    createEvent(name: "switch", value: cmd.value ? "on" : "off", type: "digital")
}

def zwaveEvent(physicalgraph.zwave.commands.manufacturerspecificv2.ManufacturerSpecificReport cmd) {
    log.debug "---MANUFACTURER SPECIFIC REPORT V2---"
    log.debug "manufacturerId:   ${cmd.manufacturerId}"
    log.debug "manufacturerName: ${cmd.manufacturerName}"
    state.manufacturer=cmd.manufacturerName
    log.debug "productId:        ${cmd.productId}"
    log.debug "productTypeId:    ${cmd.productTypeId}"
    def msr = String.format("%04X-%04X-%04X", cmd.manufacturerId, cmd.productTypeId, cmd.productId)
    updateDataValue("MSR", msr)    
    createEvent([descriptionText: "$device.displayName MSR: $msr", isStateChange: false])
}

def zwaveEvent(physicalgraph.zwave.commands.versionv1.VersionReport cmd) {
    def fw = "${cmd.applicationVersion}.${cmd.applicationSubVersion}"
    updateDataValue("fw", fw)
    log.debug "---VERSION REPORT V1--- ${device.displayName} is running firmware version: $fw, Z-Wave version: ${cmd.zWaveProtocolVersion}.${cmd.zWaveProtocolSubVersion}"
}

def zwaveEvent(physicalgraph.zwave.commands.centralscenev1.CentralSceneNotification cmd) {
    log.debug "--- CENTRAL SCENE NOTIFICATION V1 --- ignored on device ${device.displayName}: ${cmd}"
}

def zwaveEvent(physicalgraph.zwave.Command cmd) {
    log.warn "${device.displayName} received unhandled command: ${cmd}"
}

// handle commands
def configure() {
    def cmds = []
    // Get current config parameter values
    cmds << zwave.configurationV2.configurationGet(parameterNumber: 3).format()
    cmds << zwave.configurationV2.configurationGet(parameterNumber: 4).format()
    
    // Add the hub to association group 3 to get double-tap notifications
    cmds << zwave.associationV2.associationSet(groupingIdentifier: 3, nodeId: zwaveHubNodeId).format()
    cmds << zwave.associationV2.associationGet(groupingIdentifier: 3).format()
    
    delayBetween(cmds,500)
}

def updated() {
    state.oauth_token = "<Replace with smartthings oauth key>"

    if (state.lastUpdated && now() <= state.lastUpdated + 3000) return
    state.lastUpdated = now()

    if (settings.requestedGroup2 != state.currentGroup2) {
        def nodes = parseAssocGroupList(settings.requestedGroup2, 2)
        def cmds = []
        cmds << zwave.associationV2.associationRemove(groupingIdentifier: 2, nodeId: [])
        cmds << zwave.associationV2.associationSet(groupingIdentifier: 2, nodeId: nodes)
        cmds << zwave.associationV2.associationGet(groupingIdentifier: 2)
        state.currentGroup2 = settings.requestedGroup2
        sendHubCommand(cmds.collect{ new physicalgraph.device.HubAction(it.format()) }, 500)
    }

    if (settings.requestedGroup3 != state.currentGroup3) {
        state.currentGroup3 = settings.requestedGroup3
        def list = state.currentGroup3.split(',')
        if (list.size() == 1) {
            state.doubleTapUpScene = list[0]
            state.doubleTapDownScene = ""
        } else if (list.size() >= 2) {
            state.doubleTapUpScene = list[0]
            state.doubleTapDownScene = list[1]
        }
    }
}

def indicatorWhenOn() {
    sendEvent(name: "indicatorStatus", value: "when on", display: false)
    sendHubCommand(new physicalgraph.device.HubAction(zwave.configurationV2.configurationSet(configurationValue: [1], parameterNumber: 3, size: 1).format()))
}

def indicatorWhenOff() {
    sendEvent(name: "indicatorStatus", value: "when off", display: false)
    sendHubCommand(new physicalgraph.device.HubAction(zwave.configurationV2.configurationSet(configurationValue: [0], parameterNumber: 3, size: 1).format()))
}

def indicatorNever() {
    sendEvent(name: "indicatorStatus", value: "never", display: false)
    sendHubCommand(new physicalgraph.device.HubAction(zwave.configurationV2.configurationSet(configurationValue: [2], parameterNumber: 3, size: 1).format()))
}

def inverted() {
    sendEvent(name: "inverted", value: "inverted", display: false)
    sendHubCommand(new physicalgraph.device.HubAction(zwave.configurationV2.configurationSet(configurationValue: [1], parameterNumber: 4, size: 1).format()))
}

def notInverted() {
    sendEvent(name: "inverted", value: "not inverted", display: false)
    sendHubCommand(new physicalgraph.device.HubAction(zwave.configurationV2.configurationSet(configurationValue: [0], parameterNumber: 4, size: 1).format()))
}

def doubleUp() {
    sendEvent(name: "button", value: "pushed", data: [buttonNumber: 1], descriptionText: "Double-tap up (button 1) on $device.displayName", isStateChange: true, type: "digital")
}

def doubleDown() {
    sendEvent(name: "button", value: "pushed", data: [buttonNumber: 2], descriptionText: "Double-tap down (button 2) on $device.displayName", isStateChange: true, type: "digital")
}

def poll() {
    def cmds = []
    cmds << zwave.switchBinaryV1.switchBinaryGet().format()
    if (getDataValue("MSR") == null) {
        cmds << zwave.manufacturerSpecificV1.manufacturerSpecificGet().format()
    }
    delayBetween(cmds,500)
}

def refresh() {
    def cmds = []
    cmds << zwave.switchBinaryV1.switchBinaryGet().format()
    cmds << zwave.configurationV2.configurationGet(parameterNumber: 3).format()
    cmds << zwave.configurationV2.configurationGet(parameterNumber: 4).format()
    cmds << zwave.associationV2.associationGet(groupingIdentifier: 3).format()
    if (getDataValue("MSR") == null) {
        cmds << zwave.manufacturerSpecificV1.manufacturerSpecificGet().format()
    }
    delayBetween(cmds,500)
}

def on() {
    delayBetween([
        zwave.basicV1.basicSet(value: 0xFF).format(),
        zwave.switchBinaryV1.switchBinaryGet().format()
    ], 100)
}

def off() {
    delayBetween([
        zwave.basicV1.basicSet(value: 0x00).format(),
        zwave.switchBinaryV1.switchBinaryGet().format()
    ], 100)
}

// Private Methods

private parseAssocGroupList(list, group) {
    if (! list) return []
 
    if (group == 2) {  
        def nodes = []
        def nodeList = list.split(',')
        def max = 5
        def count = 0

        nodeList.each { node ->
            node = node.trim()
            if ( count >= max) {
                log.warn "Association Group ${group}: Number of members is greater than ${max}! The following member was discarded: ${node}"
            }
            else if (node.matches("\\p{XDigit}+")) {
                def nodeId = Integer.parseInt(node,16)
                if (nodeId == zwaveHubNodeId) {
                    log.warn "Association Group ${group}: Adding the hub as an association is not allowed (it would break double-tap)."
                }
                else if ( (nodeId > 0) & (nodeId < 256) ) {
                    nodes << nodeId
                    count++
                        }
                else {
                    log.warn "Association Group ${group}: Invalid member: ${node}"
                }
            }
        }

        return nodes
    } else if (group == 3) {
        return []
    } else {
        log.warn "Association Group ${group}: Invalid group: ${group}"
        return []
    }
}

