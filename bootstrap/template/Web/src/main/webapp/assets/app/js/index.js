var Index = function () {

    return {
        
        //Parallax Slider
        initParallaxSlider: function () {
			$(function() {
				$('#da-slider').cslider();
			});
        },

        //Item Slider
        initItemSlider: function () {
            $( '#mi-slider' ).catslider();
        }

    };
}();
