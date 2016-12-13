import { Component, Input, Output, EventEmitter, ElementRef,
  trigger,
  state,
  style,
  transition,
  animate } from '@angular/core';
import { ViewController, NavParams } from 'ionic-angular';

@Component({
  selector: 'page-information',
  animations: [
        trigger('visibilityChanged', [
            state('shown', style({
                opacity: 1,
                transform: 'translateX(0)'
            })),
            state('hidden', style({
                opacity: 0,
                transform: 'translateX(100%)'
            })),
            transition('shown => hidden', animate('0.2s linear')),
            transition('hidden => shown', animate('0.2s linear'))
        ])
    ],
  templateUrl: 'information.html'
})
export class InformationPage {
  visibility:string = "hidden";

  constructor(public elem:ElementRef) {}

  ionViewDidLoad() {
    
  }

  toggleVisibility() {
    if (this.visibility === "hidden") {
      this.show();   
    }
    else {
      this.hide();
    }
  }

  show() {
    this.elem.nativeElement.style.display = "block";
    this.visibility = "shown";
  }

  hide() {
    this.visibility = "hidden";
  }

  onEndAnimation($event) {
    if (this.visibility === "hidden") {
      this.elem.nativeElement.style.display = "none";
    }
  }

  onMore() {
    window.open("http://www.oilandgasinvestor.com/opfindex-information", "_system");
  }
}
