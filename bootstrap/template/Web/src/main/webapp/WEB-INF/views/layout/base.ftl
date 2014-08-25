<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <meta name="description" content="">
        <meta name="author" content="">
        <title>Bootstrap template</title>

        <!-- Bootstrap core CSS -->
        <link href="/asserts/bootstrap/css/bootstrap.min.css" rel="stylesheet">
        <!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
        <!--[if lt IE 9]>
          <script src="/asserts/lib/js/html5shiv.js"></script>
          <script src="/asserts/lib/js/respond.min.js"></script>
        <![endif]-->
        <!-- CSS Implementing Plugins -->
        <link rel="stylesheet" href="/asserts/font-awesome-4.0.3/css/font-awesome.min.css" type="text/css">
        <link rel="stylesheet" href="/asserts/lib/css/flexslider.css" type="text/css" media="screen">
        <link rel="stylesheet" href="/asserts/lib/css/parallax-slider.css" type="text/css">

        <link href="/asserts/app/css/app.css" rel="stylesheet">
        <link rel="shortcut icon" href="/asserts/app/ico/favicon.ico">
        <@layout.block name="head">
        </@layout.block>
    </head>
    <body>
        <@layout.block name="header">
            <h1>Base Layout</h1>
        </@layout.block>
        <div class="base">
            <@layout.block name="contents">
                <h2>Contents will be here</h2>
            </@layout.block>
        </div>
        <@layout.block name="footer">
            <div>Footer base must not be shown</div>
        </@layout.block>

        <!-- JS Global Compulsory -->
        <script type="text/javascript" src="/asserts/lib/js/jquery-1.8.2.min.js"></script>
        <script type="text/javascript" src="/asserts/lib/js/modernizr.custom.js"></script>
        <script type="text/javascript" src="/asserts/bootstrap/js/bootstrap.min.js"></script>

        <!-- JS Implementing Plugins -->
        <script type="text/javascript" src="/asserts/lib/js/jquery.flexslider-min.js"></script>
        <script type="text/javascript" src="/asserts/lib/js/modernizr.js"></script>
        <script type="text/javascript" src="/asserts/lib/js/jquery.cslider.js"></script>
        <script type="text/javascript" src="/asserts/lib/js/back-to-top.js"></script>
        <script type="text/javascript" src="/asserts/lib/js/jquery.sticky.js"></script>

        <script type="text/javascript" src="/asserts/app/js/app.js"></script>

        <@layout.block name="scripts">

        </@layout.block>

    </body>

</html>