<html>
<body>
<h2>Upload your image! </h2>
<form action="uploadServlet" method="post" enctype="multipart/form-data">
	Filter:<br/>
	<input type="radio" name="filter" value="1"> Grey<br>
	<input type="radio" name="filter" value="2" > Sketch<br>
	<hr/>
    <input type="file" name="file" size="25px" />
    <input type="submit" />
</form>
</body>
</html>
