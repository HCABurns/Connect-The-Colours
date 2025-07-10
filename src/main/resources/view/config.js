import { GraphicEntityModule } from './entity-module/GraphicEntityModule.js';
import {Renderer} from './modules/Renderer.js';
import { ViewportModule } from './viewport-module/ViewportModule.js';

export const modules = [
    GraphicEntityModule,
    Renderer,
    ViewportModule
];