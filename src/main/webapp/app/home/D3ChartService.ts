declare const d3: any, nv: any;

export class D3ChartConfig {
  title:
    | {
        text?: string;
        enable: boolean;
      }
    | undefined;
  chart:
    | {
        margin: {
          top: number;
          left: number;
          bottom: number;
          right: number;
        };
        useInteractiveGuideline: boolean;
        yAxis: {
          axisLabel: string;
          axisLabelDistance: number;
        };
        dispatch: {};
        xAxis: {
          axisLabel: string;
          tickFormat(d: any): any;
          showMaxMin: boolean;
        };
        x(d: any): any;
        xDomain: (number | Date)[];
        y(d: any): any;
        yDomain?: (number | Date)[];
        type: string;
        transitionDuration: number;
        height: number;
      }
    | undefined;
}

/**
 * ChartService to define the chart config for D3
 */
export class D3ChartService {
  static getChartConfig(): D3ChartConfig {
    const today = new Date();
    const priorDate = new Date().setDate(today.getDate() - 30);
    return {
      chart: {
        type: 'lineChart',
        height: 200,
        margin: {
          top: 20,
          right: 20,
          bottom: 40,
          left: 55,
        },
        x(d: any): any {
          return d.x;
        },
        y(d: any): any {
          return d.y;
        },
        useInteractiveGuideline: true,
        dispatch: {},
        xAxis: {
          axisLabel: 'Dates',
          showMaxMin: false,
          tickFormat(d: any): any {
            return d3.time.format('%b %d')(new Date(d));
          },
        },
        xDomain: [priorDate, today],
        yAxis: {
          axisLabel: '',
          axisLabelDistance: 30,
        },
        transitionDuration: 250,
      },
      title: {
        enable: true,
      },
    };
  }
}
