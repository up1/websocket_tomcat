var webSocket = null;
var queue = [];
var stockRowIndexes = {};

function onOpen(eventObject) {
	while (queue.length > 0)
		webSocket.send(queue.shift());
}

function onMessage(eventObject) {	
	var testTable = document.getElementById("testTable");

	var fields = eventObject.data.split(':');
	var tx_id = fields[0];
	var data1 = fields[1];
	var data2 = fields[2];

	// lookup the table row
	var stockRowIndex = stockRowIndexes[tx_id];
	var stockRow = testTable.rows[stockRowIndex];

	// lazily populate the table row, with 5 cells
	if (stockRow === undefined) {
		var stockRowIndex = testTable.rows.length;
		stockRow = testTable.insertRow(stockRowIndex);
		for ( var cell = 0; cell < 3; cell++) {
			stockRow.insertCell(cell);
		}
		stockRowIndexes[tx_id] = stockRowIndex;
	}

	// update the table row cell data
	stockRow.cells[0].innerHTML = tx_id;
	stockRow.cells[1].innerHTML = data1;
	stockRow.cells[2].innerHTML = data2;
}

function onClick(eventObject) {
	var username = $("#username").val();
	if (username.length > 0) {
		if ((webSocket != null) && (webSocket.readyState == 1)) {
			webSocket.send(username);
		} else {
			queue.push(username);
			if ((webSocket == null) || (webSocket.readyState > 1)) {
				webSocket = new WebSocket("ws://" + window.location.host
						+ "/TomcatWebSocket/ws/foo");
				webSocket.onopen = onOpen;
				webSocket.onmessage = onMessage;
			}
		}
	} else
		alert("Username must be at least one character.");
}

function onReady() {
	$("#subscribe").on("click", onClick);
}

$(document).ready(onReady);