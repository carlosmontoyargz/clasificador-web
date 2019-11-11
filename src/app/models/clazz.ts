export class AnalysisResult {
  original: Classes;
  suavizado: Classes;
  bayesEvaluation: string = '';
  treeEvaluation: string = '';
  suavizadoBayesEvaluation: string = '';
  suavizadoTreeEvaluation: string = '';
}

export class Classes {
  attributeSize: number;
  classes: Clazz[]
}


export class Clazz {
  name: string;
  data: number[][]
}
