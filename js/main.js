$(function () {
    $('.hyeongflex-logo').click(function () {
        $(this).addClass('on');
        $('.browser-container .imgdummy').addClass('on');
    });

    $(".toolbox .button > li").click(function () {
        if ($(this).find("ul").is(":visible")) return;

        $(".toolbox .button > li ul").stop().slideUp(300);
        $(this).find("ul").stop().slideDown(300);
        let idx = $(this).index();
        $(".tabitem img").stop().hide();
        $(".tabitem img").eq(idx).stop().fadeIn(700);
    });
    $(function () {
        $(".toolbox .button > li").click(function () {
            if ($(this).find("ul").is(":visible")) return;
            $(".toolbox .button > li").removeClass("active");
            $(this).addClass("active");
            $(".toolbox .button > li ul").stop().slideUp(300);
            $(this).find("ul").stop().slideDown(300);
            let idx = $(this).index();
            $(".tabitem img").stop().hide();
            $(".tabitem img").eq(idx).stop().fadeIn(700);
        });
        $(".toolbox .button > li").eq(0).trigger("click");
    });
    $(".toolbox .button > li").eq(0).trigger("click");
});