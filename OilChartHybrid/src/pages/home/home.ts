import { Component, ViewChild} from '@angular/core';
import {Http, Response} from '@angular/http';

import { NavController, Range, LoadingController, Loading,
  ModalController } from 'ionic-angular';
import { Toast } from 'ionic-native';
import { Utils } from '../../providers/utils';
import { ChartService } from '../../providers/chart.service';
import { IForeCaster, ForeCaster,
  IOilType, OilType,
  SelectOptions, ActionTypes } from '../../models';
import { OptionsMenuPage } from '../options-menu/options-menu';
import { InformationPage } from '../information/information';

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
  @ViewChild('menu') menu:OptionsMenuPage;
  @ViewChild('info') info:InformationPage;

  private id: string = "chartdiv";

  private data: any;

  private graphs: any;

  private currentDate: string;
  
  private oilTypes: OilType[] = [];

  chart: {[key:string]:any} = null;

  loading: boolean = true;

  options: SelectOptions;
  savingPopup:Loading;

  constructor(public navCtrl: NavController, private cs:ChartService, private utils:Utils,
    private loadingCtrl:LoadingController, private modalCtrl:ModalController) {
    this.currentDate = new Date().toISOString();
    let ids:string[] = ["0"];
    this.options = new SelectOptions();

    this.cs.getOilTypes().then(oilTypes => {
          this.options.oilTypes = oilTypes ? oilTypes:[];
          if (oilTypes && oilTypes.length > 0) {
            this.options.oilType = oilTypes[0].id;
          }          
          this.cs.getForecasters().then(forecasters => {
            this.options.forecasters = forecasters ? forecasters:[];
            this.loadChart();
          }).catch(error => {
            this.loading = false;
          });
    }).catch(error => {
          this.loading = false;
    });
  }

  onMenuToggle() {
    this.info.hide();
    this.menu.toggleVisibility();
  }

  onInformation() {
    this.menu.hide();
    this.info.toggleVisibility();
  }

  onReset() {
    this.resetChart();
  }

  onView(value) {
    this.options = value.options;
    this.loadChart();
  }

  onGoHomePage() {
    window.open("http://www.hartenergy.com", "_system");
  }

  loadChart() {
    this.loading = true;
    let ids:string[]=[];
    this.options.forecasters.forEach(forecaster => {
      if (forecaster.checked)
        ids.push(forecaster.id);
    });
    let startDate = new Date('2015-01-01').toISOString();
    let endDate = new Date().toISOString();
       
    this.cs.getChartData(ids, this.options.oilType, startDate, endDate).then(this.parseChartData.bind(this))
    .catch(error=> {
      this.loading = false;
      this.chart = null;
      alert(error);
    });
  }

  resetChart() {
    this.loading = true;
    if (this.options.oilTypes && this.options.oilTypes.length > 0) {
      this.options.oilType = this.options.oilTypes[0].id;
    }    
    this.options.forecasters.forEach((forecaster, index)=>{
      forecaster.checked = false;
      if (index == 0)
        forecaster.checked = true;
    });
    let startDate = new Date('2015-01-01').toISOString();
    let endDate = new Date().toISOString();

    let ids = [];
    if (this.options.forecasters && this.options.forecasters.length > 0) {
      ids.push(this.options.forecasters[0].id);
    }

    this.cs.getChartData(ids, this.options.oilType, startDate, endDate).then(this.parseChartData.bind(this))
    .catch(error=> {
      this.loading = false;
      this.chart = null;
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
  }

  saveDataToFile(data, type) {
    this.savingPopup = this.loadingCtrl.create({
      content:"Saving..."
    });
    this.savingPopup.present();
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
      this.savingPopup.dismiss();
      Toast.show("File saved at " + path, '5000', 'center').subscribe(
        toast => {
          console.log(toast);
        }
      );
    }, (reason) => {
      this.savingPopup.dismiss();
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
        enabled : false,
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
