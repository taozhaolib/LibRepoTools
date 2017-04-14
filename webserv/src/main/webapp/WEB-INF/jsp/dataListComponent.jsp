<script type="text/x-template" id="grid-template">            
    <div>
    <span v-for="n in getPageNum"><button v-bind:id="'page-btn-' + n" type="button" class="btn-primary" v-on:click="goToPage" >Page {{n}}</button>&nbsp;&nbsp;</span>
    <table class="table-hover table-condensed table-responsive table-striped" style="width: 100%">
        <thead>
            <tr>
                <th v-for="key in columns"
                    @click="sortBy(key)"
                    :class="{ active: sortKey == key }">
                    <a href="#">
                    {{ key | capitalize }}
                    <span class="arrow" :class="sortOrders[key] > 0 ? 'asc' : 'dsc'">
                    </a>
                    </span>
                </th>
            </tr>
        </thead>
        <tbody>
            <tr v-for="entry in filteredData">
                <td v-for="key in columns">
                    {{entry[key]}}
                </td>
            </tr>
        </tbody>
    </table>
    </div>
</script>

<script>
    Vue.component('data-grid', {
        template: '#grid-template',
        props: {
          data: Array,
          columns: Array,
          filterKey: String,
          paging: String,
          current_page: String
        },
        data: function () {
          var sortOrders = {}
          this.columns.forEach(function (key) {
            sortOrders[key] = 1
          })
          return {
            sortKey: '',
            sortOrders: sortOrders
          }
        },
        computed: {
          filteredData: function () {
            var sortKey = this.sortKey
            var filterKey = this.filterKey && this.filterKey.toLowerCase()
            var order = this.sortOrders[sortKey] || 1
            var data = this.data
            var paging = this.paging
            var pIndex = Number(this.current_page);
            
            if(paging){
                data = data.filter(function (item, index) {
                    var page = Number(paging);
                    var startPage = pIndex * page;
                    var endPage = (pIndex + 1) * page;
                    var valid = false;
                    if(index >= startPage && index < endPage){
                        valid = true;
                    }
//                    return (index >= this.index * Number(paging) && index < (this.index + 1) * Number(paging))
                    return valid;
                })
            }
            
            if (filterKey) {
              data = data.filter(function (row) {
                return Object.keys(row).some(function (key) {
                  return String(row[key]).toLowerCase().indexOf(filterKey) > -1
                })
              })
            }
            
            if (sortKey) {
              data = data.slice().sort(function (a, b) {
                a = a[sortKey]
                b = b[sortKey]
                return (a === b ? 0 : a > b ? 1 : -1) * order
              })
            }
            return data
          },
          
          getPageNum: function () {
                var data = this.data;
                var paging = Number(this.paging);
                var pageTotal = Math.ceil(data.length / paging);
                return pageTotal;
            },
            
            visible: function () {
                return this.data.length > 0 ? "visible" : "hidden"
            },
            
           pageIndex: function(){
             return this.current_page;  
           },
        },
        
        filters: {
          capitalize: function (str) {
            return str.charAt(0).toUpperCase() + str.slice(1)
          }
        },
        methods: {
          sortBy: function (key) {
            this.sortKey = key
            this.sortOrders[key] = this.sortOrders[key] * -1
          },
          
           goToPage: function(event) {
               if(event){
                   var id = event.target.id;
                   var n = id.split("-")[2]-1;
                    this.current_page = n.toString();
                }
            },
            
        }
      })

    getVueObjectForList(columns, listData, displaySearch, paging, current_page);

</script>



<style>

.table-striped>tbody>tr:nth-child(odd)>td, 
.table-striped>tbody>tr:nth-child(odd)>th {
   background-color: #c7ddef;
 }

.table-hover tbody tr:hover td, .table-hover tbody tr:hover th {
  background-color: #ddd;
}

.arrow {
  display: inline-block;
  vertical-align: middle;
  width: 0;
  height: 0;
  margin-left: 5px;
  opacity: 0.66;
}

.arrow.asc {
  border-left: 4px solid transparent;
  border-right: 4px solid transparent;
  border-bottom: 4px solid #fff;
}

.arrow.dsc {
  border-left: 4px solid transparent;
  border-right: 4px solid transparent;
  border-top: 4px solid #fff;
}
</style>