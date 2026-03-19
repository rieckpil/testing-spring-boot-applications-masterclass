import React from "react";
import {useLocation, useNavigate} from "react-router-dom";
import {connect, ConnectedProps} from 'react-redux';
import {RootState} from "./types";
import {keycloakLogin, keycloakLogout} from "./KeycloakService";
import {Button, Group, Paper, Text} from "@mantine/core";
import {IconLogin, IconLogout} from "@tabler/icons-react";

const mapState = (state: RootState) => ({
  isAuthenticated: state.authentication.isAuthenticated
});

const connector = connect(mapState);
type PropsFromRedux = ConnectedProps<typeof connector>;
type Props = PropsFromRedux & {};

const Header: React.FC<Props> = ({isAuthenticated}) => {
  const location = useLocation();
  const navigate = useNavigate();

  return (
    <Paper shadow="sm" p="md" mb="md">
      <Group justify="space-between">
        <Group>
          <Text
            fw={700}
            size="lg"
            style={{cursor: 'pointer'}}
            onClick={() => navigate('/')}
          >
            Book Reviewr
          </Text>
          <Button
            id="all-reviews"
            variant={location.pathname === '/all-reviews' ? 'filled' : 'subtle'}
            onClick={() => navigate('/all-reviews')}
          >
            All Reviews
          </Button>
        </Group>
        <Group>
          <Button
            id="submit-review"
            variant="filled"
            onClick={() => navigate('/submit-review')}
          >
            Submit a new review
          </Button>
          {isAuthenticated ? (
            <Button
              id="logout"
              color="red"
              leftSection={<IconLogout size={16}/>}
              onClick={() => keycloakLogout()}
            >
              Logout
            </Button>
          ) : (
            <Button
              id="login"
              leftSection={<IconLogin size={16}/>}
              onClick={() => keycloakLogin()}
            >
              Login
            </Button>
          )}
        </Group>
      </Group>
    </Paper>
  );
};

export default connector(Header);
