<%-- 
    Document   : institutionsSelect
    Created on : Feb 15, 2017, 9:28:08 AM
    Author     : Tao Zhao
--%>

<div id="institutionsSelectDiv" class="form-group col-lg-12 col-md-12 col-sm-12 col-xs-12" style="margin-top: 2%; margin-bottom: 4%">
    <label class="control-label">Select a affiliated institution:</label>
    <br>
    <div class="selectContainer col-lg-6 col-md-12 col-sm-12 col-xs-12" style="padding-left: 0px !important">
    <select id="affiliate" name="affiliate" class="form-control input-medium">
        <option v-for="value in institutions" v-bind:value="value" >{{value}}</option>
    </select>
    </div>
</div>


<script>
      
    new Vue({
        el: '#affiliate',
        data: {
          institutions: ${institutions}
        }
    })
</script>