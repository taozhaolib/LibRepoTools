<%-- 
    Document   : downloadLinks
    Created on : Feb 20, 2017, 11:56:11 AM
    Author     : Tao Zhao
--%>

<div id="download-link-div">

    <download-link
      v-for="(downloadLink, index) in downloadLinkData"
      v-bind:link="downloadLink"
    ></download-link>

</div>

<script type="text/x-template" id="download-link-template">
    <div style="margin-top: 5px; margin-left: 15px; margin-bottom: 5px;">
        <span class="label label-success" style="margin-right: 10px;font-size: 14px; font-variant: small-caps;">{{fileName}}</span>
        <a :href="downloadSafHref" style="margin-right: 15px;"><span>Download SAF Package</span></a>
        <a :href="importPageHref" target="_blank" ><span>Import SAF Package</span></a>
    </div>
</script>

<script>
Vue.component('download-link', {
    template: '#download-link-template',
    props: ['link'],
    data: function () {
        var fileNameArr = this.link.split("/");
        var fileName = fileNameArr[fileNameArr.length-1];
        var folder = fileNameArr[0];
        var dir = fileName.split(".")[0].split("_")[1];
        var downloadSafHref = "/webserv/download/dspace/safpackage/journal-search/"+folder+"/"+dir+"/"+fileName+"/";
        var importPageHref = "/webserv/rest/dspace/saf/page/rest-import?file="+fileName+"&safDir="+dir+"&folder="+folder+"&journalSearch=1";
        return {
            fileName: fileName,
            downloadSafHref: downloadSafHref,
            importPageHref: importPageHref,
        }
    },
})

function getDownloadLinksVue(data){

    return new Vue({
      el: '#download-link-div',
      data: {
        downloadLinkData: data
      }
    })
}
//getDownloadLinksVue(["2017.02.21.10.14.01/plos/output_plos.zip"]);
</script>