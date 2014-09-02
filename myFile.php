<?php
mysql_connect("localhost","root","");
mysql_select_db("multimediaseminar");
// $sql=mysql_query("select * from maps where EMP_NAME like 'Zee%'");
$sql=mysql_query("select * from maps");
while($row=mysql_fetch_assoc($sql))
$output[]=$row;
print(json_encode($output));// this will print the output in json
mysql_close();
?>
