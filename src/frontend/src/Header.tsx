import {Button, Icon, Menu} from "semantic-ui-react";
import React, {useState} from "react";

const App = () => {
  const [activeItem, setActiveItem] = useState();

  return (
    <Menu size={"small"} stackable>
      <Menu.Item header>Book Reviewr</Menu.Item>

      <Menu.Item
        name='allBooks'
        active={activeItem === 'allBooks'}
        onClick={() => setActiveItem('allBooks')}
      />

      <Menu.Item
        name='allReviews'
        active={activeItem === 'allReviews'}
        onClick={() => setActiveItem('allReviews')}
      />

      <Menu.Menu position='right'>
        <Menu.Item>
          <Button secondary animated='fade'>
            <Button.Content visible>Submit a new review</Button.Content>
            <Button.Content hidden>
              <Icon name='add'/>
            </Button.Content>
          </Button>
        </Menu.Item>

        <Menu.Item>
          <Button primary animated>
            <Button.Content visible>Login</Button.Content>
            <Button.Content hidden>
              <Icon name='arrow right'/>
            </Button.Content>
          </Button>
        </Menu.Item>
      </Menu.Menu>
    </Menu>
  );
}

export default App;
