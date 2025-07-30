import { GraphicEntityModule } from './entity-module/GraphicEntityModule.js';
import {Renderer} from './modules/Renderer.js';
import { ViewportModule } from './viewport-module/ViewportModule.js';
import { TooltipModule } from './tooltip-module/TooltipModule.js';


export const modules = [
    GraphicEntityModule,
    Renderer,
    ViewportModule,
    TooltipModule
];

// Debug options enabled or disabled.
export const options = [
    Renderer.defineToggle({
        // The name of the toggle
        toggle: 'debugMode',
        // The text displayed over the toggle
        title: 'DEBUG MODE',
        // The labels for the on/off states of the toggle
        values: {
            'Enabled': true, // debug on
            'Disabled': false, // debug off
        },
        // Default value of the toggle
        default: false
    })
]