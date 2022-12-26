import { Line, mixins } from 'vue-chartjs'
const { reactiveProp } = mixins

export default {
  extends: Line,
  mixins: [reactiveProp],
  data () {
    return {
     options: {
       responsive: true,
       maintainAspectRatio: false,
       onClick: function(evt, activeElements) {
         //console.log(this);
         const activePoints = this.getElementsAtEventForMode(evt, 'nearest', {
            intersect: true
          }, false);
         if(activePoints.length>0){
           const label=this.chart.data.datasets[activePoints[0]._datasetIndex].label;
           const year=this.chart.data.datasets[activePoints[0]._datasetIndex].data[activePoints[0]._index].x.getFullYear();
           const url=this.chart.data.baseurl+label+this.chart.data.query1url+year+"-00-00"+this.chart.data.query2url+year+"-00-00";
           window.open(url);
         }
      },
       title: {
         display: true, 
         text: "出現頻度上位の分布" 
       },
       tooltips: {
         mode: 'nearest', 
         intersect: "false" 
       },
       scales:{
         xAxes:[{
           display: true, 
           scaleLabel:{
             display: true,
             labelString:'出版年代'
           },
           type: 'time',
           autoSkip: false,
           time:{
             unit: 'year',
             unitSize: 600,
             displayFormats: {'year': 'YYYY'},
             tooltipFormat: 'YYYY',
           },
         }],
         yAxes:[{
           display: true, 
           scaleLabel:{
             display: false,
             labelString: '出現回数',
           },
           autoSkip: false,
           ticks:{
             min: 0,
           },
         }],
       }
     }
   }  
 },
  mounted () {
    // this.chartData is created in the mixin.
    // If you want to pass options please create a local options object
    this.renderChart(this.chartData, this.options);
  }
}