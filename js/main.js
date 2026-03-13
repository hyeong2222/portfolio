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
$(function () {

    $('header ul li').click(function () {
        $(this).addClass('on').siblings().removeClass('on')
        let idx = $(this).index()
        let sd = $('main section').eq(idx).offset().top - 114
        $('html, body').animate({
            scrollTop: sd
        })
    })
    $(window).scroll(function () {
        $('main section').each(function () {
            if ($(this).offset().top <= $(window).scrollTop() + 114) {
                let idx = $(this).index()
                $('header ul li').removeClass('on')
                $('header ul li').eq(idx).addClass('on')
            }
        })
        // header ul li 클릭했을 때 할 일
        // 클릭한 li의 색(on)
        // 해당하는 section 이동
    })
})