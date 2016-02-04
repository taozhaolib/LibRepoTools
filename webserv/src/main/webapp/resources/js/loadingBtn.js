/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


$(function() { 
   $(".btn").click(function(){
      $(this).button('loading').delay(1000).queue(function() {
        $(this).button('reset');
      });        
   });
});  

