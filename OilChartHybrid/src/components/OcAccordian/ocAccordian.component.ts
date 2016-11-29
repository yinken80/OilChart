import { Component, Input, trigger,
  state,
  style,
  transition,
  animate,
    ElementRef, AfterViewInit } from '@angular/core';

@Component({
    selector: 'ocaccordian',
    animations: [
        trigger('visibilityChanged', [
            state('shown', style({
                height: 300,
            })),
            state('hidden', style({
                height: 0
            })),
            transition('shown => hidden', animate('100ms linear')),
            transition('hidden => shown', animate('100ms linear'))
        ])
    ],
    template: `
        <div class="header">
            <div class="titleText">
                {{title}}
            </div>
            <button ion-button clear class="open" [hidden]="opened" (click)="onOpen()">
                <ion-icon ios="ios-arrow-down" md="ios-arrow-down"></ion-icon>
            </button>
            <button ion-button clear class="close" [hidden]="!opened" (click)="onClose()">
                <ion-icon ios="ios-arrow-up" md="ios-arrow-up"></ion-icon>
            </button>            
        </div>
        <div class="accordian-content" [@visibilityChanged]="visibility">
            <ng-content></ng-content>
        </div>`
})
export class OcAccordian implements AfterViewInit{
    @Input('title') title: string;

    constructor(private elem:ElementRef) {

    }

    opened:boolean = true;
    visibility:string="shown";
    componentHeight:number = 0;
    onClose() {
        this.opened = false;
        this.visibility = "hidden";
    }

    onOpen() {
        this.opened = true;
        this.visibility = "shown";
    }

    ngAfterViewInit() {
        this.componentHeight = this.elem.nativeElement.height;
    }
}