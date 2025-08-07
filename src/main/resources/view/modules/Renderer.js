import { api as entityModule } from '../entity-module/GraphicEntityModule.js';

export class Renderer {
  static get moduleName() {
    return 'Renderer';
  }

  static defineToggle(option) {
          Renderer.toggles[option.toggle] = false
          option.get = () => Renderer.toggles[option.toggle]
          Renderer.toggles[option.toggle] = false
          option.set = (value) => {
              Renderer.toggles[option.toggle] = value
              Renderer.refreshContent()
          }
          return option
      }

  static refreshContent(){

    if (Renderer.toggles.debugMode) {
        const group = entityModule.entities.get(2)
        const debug_group = entityModule.entities.get(3)

        group.container.interactive = false;
        group.container.visible = false;

        debug_group.container.visible = true;

    }
    else{

        const group = entityModule.entities.get(2)

        group.container.interactive = true;
        group.container.visible = true;

        const debug_group = entityModule.entities.get(3)
        debug_group.container.visible = false;
    }
  }


  handleFrameData(frameInfo, frameData) {

    if (frameData){
    this.lastTiles = frameData.tiles || [];
    }
    return {
      frameInfo,
      frameData,
    };
  }


  updateScene(previousData, currentData, progress) {
  if (currentData && currentData.frameData && currentData.frameData.tiles){
    // Total number of tiles to animate
    const tiles = currentData.frameData.tiles
    const totalTiles = tiles.length;

    // How many tiles should be visible at this frame based on progress (0 to 1)
    const tilesToShow = Math.floor(progress * totalTiles);
    for (let i = 0; i < tilesToShow; i++) {
        const tile = tiles[i];
        const entity = entityModule.entities.get(tile.id);
        if (entity) {
          entity.graphics.texture = PIXI.Texture.from(tile.texture);
        }
    }
  }
  Renderer.refreshContent()
}

  reinitScene(){}

  animateScene (delta) {}

  handleGlobalData (players, globalData) {
      this.globalData = {
        players: players,
        playerCount: players.length
      }
    }

}

Renderer.toggles = {}
Renderer.optionValues = {}
Renderer.debugChildren = [];