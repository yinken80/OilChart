import { Component } from '@angular/core';
import { Platform } from 'ionic-angular';
import { StatusBar, Splashscreen } from 'ionic-native';

import { HomePage } from '../pages/home/home';
import { ChartService } from '../providers/chart.service';


@Component({
  template: `<ion-nav [root]="rootPage"></ion-nav>`
})
export class MyApp {
  rootPage = HomePage;

  constructor(platform: Platform, private cs:ChartService) {
    platform.ready().then(() => {
      // Okay, so the platform is ready and our plugins are available.
      // Here you can do any higher level native things you might need.
      StatusBar.styleDefault();
      setTimeout(() => {
        Splashscreen.hide()
      }, 100);

      document.addEventListener('pause', () => {
        this.saveState();
      }, false);

      document.addEventListener('resume', () => {
        this.restoreState();
      }, false);
    });
  }

  saveState() {
    this.cs.saveSelectOptions();
  }

  restoreState() {
    //this.cs.loadSelectOptions();
  }
}
