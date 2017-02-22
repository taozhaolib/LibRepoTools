<%-- 
    Document   : dateRangePicker
    Created on : Feb 14, 2017, 4:50:51 PM
    Author     : Tao Zhao
--%>

<div id="dateRangePickerDiv" class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
  <date-range-picker></date-range-picker>
</div>

<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datepicker/1.6.4/js/bootstrap-datepicker.min.js"></script>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datepicker/1.6.4/css/bootstrap-datepicker3.css"/>

<script>
      Vue.component('date-range-picker', {
        template: '<table style="width: 60%" >' +
                    '<tbody>' +
                        '<tr>' +
                            '<td>' +
                                '<div class="form-group">' +
                                  '<label class="control-label" for="startDate">Start Date</label>' +
                                  '<input class="form-control" id="startDate" name="startDate" placeholder="YYYY-MM-DD" value="'+startDate+'" type="text"/>' +
                                '</div>' +
                            '</td>' +
                            '<td style="width:10%"></td>' +
                            '<td>' +
                                '<div class="form-group">' +
                                  '<label class="control-label" for="endDate">End Date</label>' +
                                  '<input class="form-control" id="endDate" name="endDate" placeholder="YYYY-MM-DD" value="'+endDate+'" type="text"/>' +
                                '</div>' +
                            '</td>' +
                        '</tr>' +
                    '</tbody>' +
                '</table>'
      }),
    
      // create a root instance
      new Vue({
        el: '#dateRangePickerDiv'
      })
</script>

<script>
    $(document).ready(function(){
      var date_input=$('input[name$="Date"]');
      var container=$('.bootstrap-iso form').length>0 ? $('.bootstrap-iso form').parent() : "body";
      var options={
        format: 'yyyy-mm-dd',
        container: container,
        todayHighlight: true,
        autoclose: true,
      };
      date_input.datepicker(options);
    })
</script>