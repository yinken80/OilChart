import { Component, Input, Output, EventEmitter, ElementRef,
  trigger,
  state,
  style,
  transition,
  animate } from '@angular/core';
import { ViewController, NavParams } from 'ionic-angular';
import { SelectOptions, ActionTypes } from '../../models';

@Component({
  selector: 'page-options-menu',
  animations: [
        trigger('visibilityChanged', [
            state('shown', style({
                opacity: 1,
                transform: 'translateX(0)'
            })),
            state('hidden', style({
                opacity: 0,
                transform: 'translateX(-100%)'
            })),
            transition('shown => hidden', animate('0.2s linear')),
            transition('hidden => shown', animate('0.2s linear'))
        ])
    ],
  templateUrl: 'options-menu.html'
})
export class OptionsMenuPage {
  @Input() options:SelectOptions;
  @Output() reset = new EventEmitter();
  @Output() view = new EventEmitter();
  private loading:boolean = false;
  visibility:string="hidden";

  constructor(private elem:ElementRef) {
    
  }

  ionViewDidLoad() {
    
  }

  onReset() {
    this.reset.emit();
  }

  onView() {
    this.view.emit({options:this.options});
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

  onClickMain() {
    this.toggleVisibility();
  }
}
