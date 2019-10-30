import { Component, OnInit } from '@angular/core';

import * as Highcharts from 'highcharts';
import highcharts3D from 'highcharts/highcharts-3d.src';
import {DataSetService} from "../../services/data-set.service";
highcharts3D(Highcharts);

declare var require: any;
let Boost = require('highcharts/modules/boost');
let noData = require('highcharts/modules/no-data-to-display');
let More = require('highcharts/highcharts-more');

Boost(Highcharts);
noData(Highcharts);
More(Highcharts);
noData(Highcharts);

@Component({
  templateUrl: 'dashboard.component.html'
})
export class DashboardComponent implements OnInit {

  public options: any = {
    chart: {
      type: 'scatter',
      marginBottom: 100,
      marginRight: 50,
      options3d: {
        enabled: true,
        alpha: 20,
        beta: 30,
        depth: 650,
        viewDistance: 7,
        frame:{
          bottom :{
            size: 1,
            color: 'rgba(0, 0, 0, 0.02)'
          },
          back :{
            size: 1,
            color: 'rgba(0, 0, 0, 0.04)'
          },
          side :{
            size: 1,
            color: 'rgba(0, 0, 0, 0.06)'
          }
        }
      }
    },
    title : {
      text: '3D Scatter Plot'
    },
    xAxis:{
      min:0,
      max:10
    },
    yAxis:{
      min:0,
      max:10
    },
    zAxis:{
      min:0,
      ax:10
    },
    series : []
    /*[{
      name: 'Reading',
      data: [
        [1, 6, 5], [8, 7, 9], [1, 3, 4], [4, 6, 8], [5, 7, 7], [6, 9, 6],
        [7, 0, 5], [2, 3, 3], [3, 9, 8], [3, 6, 5], [4, 9, 4], [2, 3, 3],
        [6, 9, 9], [0, 7, 0], [7, 7, 9], [7, 2, 9], [0, 6, 2], [4, 6, 7],
        [3, 7, 7], [0, 1, 7], [2, 8, 6], [2, 3, 7], [6, 4, 8], [3, 5, 9],
        [7, 9, 5], [3, 1, 7], [4, 4, 2], [3, 6, 2], [3, 1, 6], [6, 8, 5],
        [6, 6, 7], [4, 1, 1], [7, 2, 7], [7, 7, 0], [8, 8, 9], [9, 4, 1],
        [8, 3, 4], [9, 8, 9], [3, 5, 3], [0, 2, 4], [6, 0, 2], [2, 1, 3],
        [5, 8, 9], [2, 1, 1], [9, 7, 6], [3, 0, 2], [9, 9, 0], [3, 4, 8],
        [2, 6, 1], [8, 9, 2], [7, 6, 5], [6, 3, 1], [9, 3, 1], [8, 9, 3],
        [9, 1, 0], [3, 8, 7], [8, 0, 0], [4, 9, 7], [8, 6, 2], [4, 3, 0],
        [2, 3, 5], [9, 1, 4], [1, 1, 4], [6, 0, 2], [6, 1, 6], [3, 8, 8],
        [8, 8, 7], [5, 5, 0], [3, 9, 6], [5, 4, 3], [6, 8, 3], [0, 1, 5],
        [6, 7, 3], [8, 3, 2], [3, 8, 3], [2, 1, 6], [4, 6, 7], [8, 9, 9],
        [5, 4, 2], [6, 1, 3], [6, 9, 5], [4, 8, 2], [9, 7, 4], [5, 4, 2],
        [9, 6, 1], [2, 7, 3], [4, 5, 4], [6, 8, 1], [3, 4, 0], [2, 2, 6],
        [5, 1, 2], [9, 9, 7], [6, 9, 9], [8, 4, 3], [4, 1, 7], [6, 2, 5],
        [0, 4, 9], [3, 5, 9], [6, 9, 1], [1, 9, 2]
      ]
    }]*/
  };
  constructor(private dataSetService: DataSetService) {}

  ngOnInit(): void {
    this.dataSetService
      .getClasses('seg-data.txt')
      .subscribe(
        classes => {
          console.log('Se han descargado las clases');
          console.log(classes);
          classes.forEach(c => this.options.series.push(c));
          this.inicializarChart()
        },
        error => {
          console.log(`Ocurrio un error al descargar las clases: ${error}`);
        });
    this.inicializarChart();
  }

  private inicializarChart() {
    let chart = Highcharts.chart('container', this.options);
    let dragStart = eStart => {
      eStart = chart.pointer.normalize(eStart);

      let posX = eStart.chartX,
        posY = eStart.chartY,
        alpha = chart.options.chart.options3d.alpha,
        beta = chart.options.chart.options3d.beta,
        sensitivity = 4,  // lower is more sensitive
        handlers = [];

      function drag(e) {
        // Get e.chartX and e.chartY
        e = chart.pointer.normalize(e);

        chart.update({
          chart: {
            options3d: {
              alpha: alpha + (e.chartY - posY) / sensitivity,
              beta: beta + (posX - e.chartX) / sensitivity
            }
          }
        }, undefined, undefined, false);
      }
      handlers.push(Highcharts.addEvent(document, 'mousemove', drag));
      handlers.push(Highcharts.addEvent(document, 'touchmove', drag));


      function unbindAll() {
        handlers.forEach(function (unbind) {
          if (unbind) {
            unbind();
          }
        });
        handlers.length = 0;
      }
      handlers.push(Highcharts.addEvent(document, 'mouseup', unbindAll));
      handlers.push(Highcharts.addEvent(document, 'touchend', unbindAll));
    };
    Highcharts.addEvent(chart.container, 'mousedown', dragStart);
    Highcharts.addEvent(chart.container, 'touchstart', dragStart);
  }
}
