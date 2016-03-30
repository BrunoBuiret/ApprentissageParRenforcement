package agent.strategy;

import java.util.List;
import java.util.Random;

import agent.rlagent.RLAgent;
import environnement.Action;
import environnement.Etat;
import environnement.gridworld.ActionGridworld;
import java.util.ArrayList;

/**
 * Strategie qui renvoit une action aleatoire avec probabilite epsilon, une
 * action gloutonne (qui suit la politique de l'agent) sinon Cette classe a
 * acces a un RLAgent par l'intermediaire de sa classe mere.
 *
 * @author lmatignon
 *
 */
public class StrategyGreedy extends StrategyExploration {
    protected double epsilon;

    private Random rand = new Random();

    public StrategyGreedy(RLAgent agent, double epsilon) {
        super(agent);
        this.epsilon = epsilon;
    }

    /**
     * @param _e
     * @return action selectionnee par la strategie d'exploration
     */
    @Override
    public Action getAction(Etat _e) {
        //getAction renvoi null si _e absorbant
        if (this.agent.getEnv().estAbsorbant()) {
            return null;
        }
        
        if (rand.nextDouble() < this.epsilon) {
            List<Action> allActions = new ArrayList<>();
            allActions.add(ActionGridworld.NORD);
            allActions.add(ActionGridworld.SUD);
            allActions.add(ActionGridworld.EST);
            allActions.add(ActionGridworld.OUEST);
            
            return allActions.get(rand.nextInt(4));
        } else {
            List<Action> politique = this.agent.getPolitique(_e);
            if ( politique.size() > 0) {
                return politique.get(0);
            } else {
                return null;
            }
        }
    }

    public void setEpsilon(double epsilon) {
        this.epsilon = epsilon;
    }
}
