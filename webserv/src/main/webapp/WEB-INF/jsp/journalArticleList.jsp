<%-- 
    Document   : journalArticleList
    Created on : Feb 16, 2017, 9:36:08 AM
    Author     : Tao Zhao
--%>
<script>    
    var listData = ${articles};
    var displaySearch = true;
    var paging = 20;
    var current_page = 0;
</script>

<!-- condition: emptyData is used to handle the situation of empty data found -->

<div id="listDiv" class=" col-lg-12 col-md-12 col-sm-12 ">  
    <template v-if="emptyData">
        <h4>No results to display</h4>
    </template>
    <template v-else="emptyData">
        <h4>List of the search Results</h4>
        <template v-if="displaySearch">
                Search <input name="query" v-model="searchQuery">                
        </template>
        <br><br>
        <data-grid
            :current_page="current_page"
            :paging="paging"
            :data="gridData"
            :columns="gridColumns"
            :filter-key="searchQuery">
        </data-grid>
    </template>
</div>

<%@include file= "dataListComponent.jsp" %>