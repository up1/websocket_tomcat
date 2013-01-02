<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html" pageEncoding="utf-8" isELIgnored="false" %>
<html xmlns="http://www.w3.org/1999/xhtml">
        <head>
                <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
                <script src="http://code.jquery.com/jquery-1.7.2.min.js" type="text/javascript"></script>
                <script src="javascript/websockets.js" type="text/javascript"></script>
                <title>WebSockets using Apache Tomcat v7.0.27+</title>
        </head>
        <body>
                <h3>User</h3>
                <input id="username" type="text" value=""/><button id="subscribe">Subscribe</button>
                <br />
                <h3>Data from server</h3>
                
                <table id="testTable" border="1" >
				<thead>
					<tr>
						<th>ID</th>
						<th>Data 1</th>
						<th>Data 2</th>
					</tr>
				</thead>
				<tbody>
				</tbody>
		</table>
                
        </body>
</html>