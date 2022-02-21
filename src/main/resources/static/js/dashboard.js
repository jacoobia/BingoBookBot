const navs = [ "#nav-home", "#nav-servers", "#nav-stats" ];
const pages = [ "#content-home", '#content-servers', '#content-stats' ];
let activePage = 0;
const navClass = "active";
const hide = 'hidden';

$(document).on('load', function () {
    setInterval(function () {
        $('#content-servers').load(window.location.href + '#content-servers');
    }, 3000);
});

function openTab(index) {
    if(index !== activePage) {
        $(navs[activePage]).removeClass(navClass);
        $(pages[activePage]).addClass(hide);

        $(navs[index]).addClass(navClass);
        $(pages[index]).removeClass(hide);

        activePage = index;
    }
}

function openAuth() {
    let newWindow = window.open('/oauth2/authorization/discord', 'name', 'menubar=1,resizable=0,height=850,width=450');
    if (window.focus) {
        newWindow.focus();
    }
}

function clickServer(guildId, ) {
    let data = { id: guildId };

    $.ajax({
        type : "POST",
        url : "/test",
        data : JSON.stringify(data),
        contentType: "application/json",
        dataType: "json",
        success : function() {
            console.log('not frick!!');
        },
        error : function(e) {
            console.log('frick\n' + e);
        }
    });
}