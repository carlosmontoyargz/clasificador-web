export class AnalysisResult {
  original: Classes;
  suavizado: Classes;
}

export class Classes {
  attributeSize: number;
  classes: Clazz[]
}


export class Clazz {
  name: string;
  data: number[][]
}
