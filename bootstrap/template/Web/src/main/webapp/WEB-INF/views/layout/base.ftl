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
        <link href="/assets/bootstrap/css/bootstrap.min.css" rel="stylesheet">
        <!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
        <!--[if lt IE 9]>
          <script src="/assets/lib/js/html5shiv.js"></script>
          <script src="/assets/lib/js/respond.min.js"></script>
        <![endif]-->
        <!-- CSS Implementing Plugins -->
        <link rel="stylesheet" href="/assets/font-awesome-4.0.3/css/font-awesome.min.css" type="text/css">
        <link rel="stylesheet" href="/assets/lib/css/flexslider.css" type="text/css" media="screen">
        <link rel="stylesheet" href="/assets/lib/css/parallax-slider.css" type="text/css">

        <link href="/assets/app/css/app.css" rel="stylesheet">
        <link rel="shortcut icon" href="/assets/app/ico/favicon.ico">
        <@layout.block name="head">
        </@layout.block>
    </head>
    <body>
        <@layout.block name="header">
            <!-- Fixed navbar -->
            <div class="navbar navbar-default navbar-fixed-top" role="navigation">
              <div class="container">
                <div class="navbar-header">
                  <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target=".navbar-collapse">
                    <span class="sr-only">Toggle navigation</span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                  </button>
                  <a class="navbar-brand" href="#">Project name</a>
                </div>
                <div class="collapse navbar-collapse">
                  <ul class="nav navbar-nav">
                    <li class="active"><a href="#">Home</a></li>
                    <li><a href="#about">About</a></li>
                    <li><a href="#contact">Contact</a></li>
                    <li class="dropdown">
                      <a href="#" class="dropdown-toggle" data-toggle="dropdown">Dropdown <span class="caret"></span></a>
                      <ul class="dropdown-menu" role="menu">
                        <li><a href="#">Action</a></li>
                        <li><a href="#">Another action</a></li>
                        <li><a href="#">Something else here</a></li>
                        <li class="divider"></li>
                        <li class="dropdown-header">Nav header</li>
                        <li><a href="#">Separated link</a></li>
                        <li><a href="#">One more separated link</a></li>
                      </ul>
                    </li>
                  </ul>
                </div><!--/.nav-collapse -->
              </div>
            </div>
        </@layout.block>
        <div class="container">
            <@layout.block name="contents">
                <h2>Contents will be here</h2>
            </@layout.block>
        </div>
        <@layout.block name="footer">
        <div class="footer">
          <div class="container">
            <p class="text-muted">Place sticky footer content here.</p>
          </div>
        </div>
        </@layout.block>

        <!-- JS Global Compulsory -->
        <script type="text/javascript" src="/assets/lib/js/jquery-1.8.2.min.js"></script>
        <script type="text/javascript" src="/assets/lib/js/modernizr.custom.js"></script>
        <script type="text/javascript" src="/assets/bootstrap/js/bootstrap.min.js"></script>
        <!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->
        <script type="text/javascript" src="/assets/bootstrap/js/ie10-viewport-bug-workaround.js"></script>
        <!-- JS Implementing Plugins -->
        <script type="text/javascript" src="/assets/lib/js/jquery.flexslider-min.js"></script>
        <script type="text/javascript" src="/assets/lib/js/modernizr.js"></script>
        <script type="text/javascript" src="/assets/lib/js/jquery.cslider.js"></script>
        <script type="text/javascript" src="/assets/lib/js/back-to-top.js"></script>
        <script type="text/javascript" src="/assets/lib/js/jquery.sticky.js"></script>

        <script type="text/javascript" src="/assets/app/js/app.js"></script>

        <@layout.block name="scripts">

        </@layout.block>

    </body>

</html>