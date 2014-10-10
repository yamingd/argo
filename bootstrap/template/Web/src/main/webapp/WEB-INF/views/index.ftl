<@layout.extends name="layout/base.ftl">

    <@layout.put block="contents" type="replace">
        <div class="page-header">
                <h1>Sticky footer with fixed navbar. 中文测试</h1>
              </div>
              <p class="lead">Pin a fixed-height footer to the bottom of the viewport in desktop browsers with this custom HTML and CSS. A fixed navbar has been added with <code>padding-top: 60px;</code> on the <code>body > .container</code>.</p>
              <p>Back to <a href="../sticky-footer">the default sticky footer</a> minus the navbar.</p>
    </@layout.put>

    <@layout.put block="scripts">

    </@layout.put>
</@layout.extends>