import { Component, ViewChild } from '@angular/core';
import {Http, Response} from '@angular/http';

import { NavController } from 'ionic-angular';
import { Toast } from 'ionic-native';
import { Utils } from '../../providers/utils';
import { ChartService } from '../../providers/chart.service';
import { IForeCaster, ForeCaster } from '../../models/forecaster.model';
import { SelectOptions } from '../../models/selectOptions.model';
import { OcAccordian } from '../../components/OcAccordian/ocAccordian.component';

interface Configuration {
  dataProvider: Array<any>;
  graphs: Array<any>;
  currentDate: string;
  listener: () => void;
  saveFs: any;
}
@Component({
  selector: 'page-home',
  templateUrl: 'home.html'
})
export class HomePage {
  @ViewChild('menu') menu:OcAccordian;

  private id: string = "chartdiv";

  private data: any;

  private graphs: any;

  private currentDate: string = "09/01/2015";

  oilTypes: string[] = ["Global Oil Price Avg", "West Texas Inter. (WTI)", "Brent"];
  forecasterNames: string[] = ["Hart Energy Forecaster Index", "ABN AMRO", "Bank of America", "Bernstein Research",
        "Bloomberg", "BNP Paribas"];
  forecasterIds: string[] = ["0", "103", "6", "5", "65", "57"];
  minDate: number;
  maxDate: number;

  chart: {[key:string]:any};

  loading: boolean = true;

  options: SelectOptions;

  constructor(public navCtrl: NavController, private cs:ChartService, private utils:Utils) {
    let ids:string[] = ["0"];
    this.minDate = new Date(2015, 1, 1).getTime();
    this.maxDate = new Date().getTime();
    this.currentDate = new Date().toISOString();

    this.options = new SelectOptions();
    this.options.dateRange.lower = this.minDate;
    this.options.dateRange.upper = this.maxDate;

    this.forecasterNames.forEach((value:string, index:number) => {
      let fc:ForeCaster = new ForeCaster();
      if (index == 0) {
        fc.checked = true;
      }

      fc.text = value;
      fc.id = this.forecasterIds[index];

      this.options.forecasters.push(fc);
    });

    this.cs.getChartData(ids, 0, "2015-01-01", "2016-11-29").then(this.parseChartData.bind(this))
      .catch(error=> {
        this.loading = false;
        alert(error);
      });
  }

  parseChartData(value) {
    this.data = value.dataProvider;
    this.graphs = value.graphs;
    this.chart = this.makeChart({
      dataProvider: this.data,
      graphs: this.graphs,
      currentDate: this.currentDate,
      listener: this.onFinishLoad.bind(this),
      saveFs: this.saveDataToFile.bind(this)
    });
    this.loading = false;
  }

  onFinishLoad(e) {
    this.loading = false;
    console.log("loaded");
  }

  onChangeDate() {
    var startDate, endDate:Date;
  }

  onView() {
    //this.menu.onClose();
    this.loading = true;
    let ids:string[]=[];
    this.options.forecasters.forEach(forecaster => {
      if (forecaster.checked)
        ids.push(forecaster.id);
    });
    let startDate = new Date(this.options.dateRange.lower).toISOString();
    let endDate = new Date(this.options.dateRange.upper).toISOString();
    this.cs.getChartData(ids, this.options.oilType, startDate, endDate).then(this.parseChartData.bind(this))
    .catch(error=> {
      this.loading = false;
      alert(error);
    });
  }

  onReset() {
    this.loading = true;
    this.options.oilType = 0;
    this.options.forecasters.forEach((forecaster, index)=>{
      forecaster.checked = false;
      if (index == 0)
        forecaster.checked = true;
    });
    this.options.dateRange.upper = this.minDate;
    this.options.dateRange.lower = this.maxDate;
    let startDate = new Date(this.minDate).toISOString();
    let endDate = new Date(this.maxDate).toISOString();

    this.cs.getChartData(["0"], this.options.oilType, startDate, endDate).then(this.parseChartData.bind(this))
    .catch(error=> {
      this.loading = false;
      alert(error);
    });
  }

  saveDataToFile(data, type) {
    // Split the base64 string in data and contentType
    var block = data.split(";");
    console.log(block);
    // Get the content type
    var dataType = block[0].split(":")[1];// In this case "image/png"
    // get the real base64 content of the file
    var realData = block[1].split(",")[1];
    
    var DataBlob = this.base64ToBlob(realData, dataType);
    var name = new Date().getTime().toString(10);
    if (dataType === "image/jpeg") {
      name = name + ".jpg";
    } else if (dataType === "image/png") {
      name = name + ".png";
    }

    this.utils.saveFileTo(name, DataBlob).then((path) => {
      Toast.show("File saved at " + path, '5000', 'center').subscribe(
        toast => {
          console.log(toast);
        }
      );
    }, (reason) => {
      Toast.show("Failed to save chart as an image", '5000', 'center').subscribe(
        toast => {
          console.log(toast);
        }
      );
    });
  }

  base64ToBlob(b64Data, contentType, sliceSize?) {
    contentType = contentType || '';
    sliceSize = sliceSize || 512;
    var byteCharacters = atob(b64Data);
    var byteArrays = [];

    for (var offset = 0; offset < byteCharacters.length; offset += sliceSize) {
        var slice = byteCharacters.slice(offset, offset + sliceSize);

        var byteNumbers = new Array(slice.length);
        for (var i = 0; i < slice.length; i++) {
            byteNumbers[i] = slice.charCodeAt(i);
        }

        var byteArray = new Uint8Array(byteNumbers);

        byteArrays.push(byteArray);
    }

    var blob = new Blob(byteArrays, {type: contentType});
    return blob;
  }

  makeChart({ dataProvider, graphs, currentDate, listener, saveFs }: Configuration) {
    return {
      type: "serial",
      categoryField: "date",
      autoMarginOffset: 1,
      marginRight: 1,
      marginTop: 1,
      startDuration: 0,
      fontSize: 13,
      theme: "light",
      titles: [
        {
          text: "Oil Pricing Forecast Index",
          size: 15
        },
        {
          text: "Avg. Brent & WTI Price Type",
          size: 10
        }
      ],
      balloon: {
        hideBalloonTime: 1000, // 1 second
        disableMouseEvents: false, // allow click
        fixedPosition: true,
      },
      categoryAxis: {
        gridPosition: "start",
        gridThickness: 0,
        showFirstLabel: true,
        showLastLabel: true,
        labelsEnabled: true,
        minHorizontalGap: 30,
        autoGridCount: true,
        labelRotation: 30,
        labelOffset: 5,
        offset: 3,
        fontSize: 9,
        parseDates: true,
        equalSpacing: true,
        dataDateFormat: "MM/DD/YYYY",
        boldPeriodBeginning: true,
        guides: [
          {
            date: currentDate,
            lineColor: "#CC0000",
            lineAlpha: 1,
            dashLength: 2,
            inside: true,
            labelRotation: -90,
            position: "bottom",
            label: "Now",
            fontSize: 12,
            lineThickness: 1
          }
        ]
      },
      trendLines: [
      ],
      chartCursor: {
        enabled: true,
        oneBalloonOnly: true,
        bulletsEnabled: true,
        bulletSize: 8
      },
      legend: {
        useGraphSettings: true,
        align: "left",
        enabled: true,
        rollOverGraphAlpha: 0.23,
        valueAlign: "left",
        verticalGap: 1,
        markerSize: 7,
        marginLeft: 1,
        fontSize: 10,
        valueWidth: 0,
        labelWidth: 250,
        marginRight: 0,
        marginTop: 2,
        spacing: 10,
        equalWidths: false,
        position: "bottom"
      },
      graphs: graphs,
      guides: [],
      valueAxes: [
        {
          id: "ValueAxis-1",
          tickLength: 3,
          fontSize: 10,
          labelFunction: function formatValue(value, formattedValue, valueAxis) { return "$ " + value; }
        }
      ],
      allLabels: [],
      dataProvider: dataProvider,
      listeners: [{
        event: "rendered",
        method: listener
      }],
      export: {
        enabled : true,
        menu: [{
          class: "export-main",
          menu: [{
            label: "PNG",
            click: function() {
              this.capture({}, function() {
                this.toPNG({}, function(data) {
                  saveFs(data, "png");
                });
              });
            }
          }, {
            label: "JPG",
            click: function() {
              this.capture({}, function() {
                this.toJPG({}, function(data) {
                  saveFs(data, "jpg");
                });
              });
            }
          }]
        }]
      }
    };
  };
}
